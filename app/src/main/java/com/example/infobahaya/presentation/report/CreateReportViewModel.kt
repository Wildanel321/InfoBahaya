package com.example.infobahaya.presentation.report

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.infobahaya.core.di.ServiceLocator
import com.example.infobahaya.domain.model.DraftReport
import com.example.infobahaya.domain.model.HazardCategory
import com.example.infobahaya.domain.model.HazardReport
import com.example.infobahaya.domain.model.HazardSeverity
import com.example.infobahaya.domain.model.ReportStatus
import com.example.infobahaya.domain.repository.AuthRepository
import com.example.infobahaya.domain.repository.HazardReportRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

data class CreateReportUiState(
    val currentStep: Int = 1, // 1 to 7
    val category: HazardCategory? = null,
    val photoUrls: List<String> = emptyList(),
    val hasVideo: Boolean = false,
    val latitude: Double = -6.2088,
    val longitude: Double = 106.8456,
    val address: String = "Jl. Menteng Raya No. 18, Menteng, Jakarta Pusat",
    val landmark: String = "",
    val severity: HazardSeverity = HazardSeverity.SEDANG,
    val title: String = "",
    val description: String = "",
    val stepError: String? = null,
    val isLoading: Boolean = false,
    val isSubmitted: Boolean = false,
    val createdReport: HazardReport? = null,
    val hasSavedDraft: Boolean = false,
    val isOfflineMode: Boolean = false
)

class CreateReportViewModel(
    private val hazardReportRepository: HazardReportRepository = ServiceLocator.provideHazardReportRepository(),
    private val authRepository: AuthRepository = ServiceLocator.provideAuthRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreateReportUiState())
    val uiState = _uiState.asStateFlow()

    init {
        checkExistingDraft()
        checkOfflineState()
    }

    private fun checkOfflineState() {
        viewModelScope.launch {
            hazardReportRepository.isOfflineMode().collect { offline ->
                _uiState.value = _uiState.value.copy(isOfflineMode = offline)
            }
        }
    }

    private fun checkExistingDraft() {
        viewModelScope.launch {
            val draft = hazardReportRepository.getDraft().firstOrNull()
            if (draft != null && draft.category != null) {
                _uiState.value = _uiState.value.copy(
                    hasSavedDraft = true,
                    category = draft.category,
                    photoUrls = draft.photoUris,
                    latitude = draft.latitude ?: _uiState.value.latitude,
                    longitude = draft.longitude ?: _uiState.value.longitude,
                    address = draft.address.ifBlank { _uiState.value.address },
                    landmark = draft.landmark,
                    severity = draft.severity,
                    title = draft.title,
                    description = draft.description,
                    currentStep = draft.currentStep.coerceIn(1, 6)
                )
            }
        }
    }

    fun onSelectCategory(cat: HazardCategory) {
        _uiState.value = _uiState.value.copy(category = cat, stepError = null)
        saveCurrentDraft()
    }

    fun addSamplePhoto(url: String) {
        val current = _uiState.value.photoUrls.toMutableList()
        current.add(url)
        _uiState.value = _uiState.value.copy(photoUrls = current, stepError = null)
        saveCurrentDraft()
    }

    fun removePhoto(index: Int) {
        val current = _uiState.value.photoUrls.toMutableList()
        if (index in current.indices) {
            current.removeAt(index)
            _uiState.value = _uiState.value.copy(photoUrls = current)
            saveCurrentDraft()
        }
    }

    fun setLocation(lat: Double, lng: Double, address: String, landmark: String) {
        _uiState.value = _uiState.value.copy(
            latitude = lat,
            longitude = lng,
            address = address,
            landmark = landmark,
            stepError = null
        )
        saveCurrentDraft()
    }

    fun refreshGpsLocation() {
        // Simulated GPS refresh for high-accuracy location
        _uiState.value = _uiState.value.copy(
            latitude = -6.2088 + (Math.random() - 0.5) * 0.005,
            longitude = 106.8456 + (Math.random() - 0.5) * 0.005,
            address = "Jl. Sudirman No. ${(10..90).random()}, Jakarta Pusat",
            stepError = null
        )
    }

    fun onSelectSeverity(sev: HazardSeverity) {
        _uiState.value = _uiState.value.copy(severity = sev, stepError = null)
        saveCurrentDraft()
    }

    fun onTitleChange(newTitle: String) {
        _uiState.value = _uiState.value.copy(title = newTitle, stepError = null)
        saveCurrentDraft()
    }

    fun onDescriptionChange(newDesc: String) {
        _uiState.value = _uiState.value.copy(description = newDesc, stepError = null)
        saveCurrentDraft()
    }

    fun onLandmarkChange(newLandmark: String) {
        _uiState.value = _uiState.value.copy(landmark = newLandmark)
        saveCurrentDraft()
    }

    fun nextStep() {
        val s = _uiState.value
        when (s.currentStep) {
            1 -> {
                if (s.category == null) {
                    _uiState.value = s.copy(stepError = "Pilih kategori bahaya terlebih dahulu")
                    return
                }
            }
            2 -> {
                if (s.photoUrls.isEmpty()) {
                    // Auto provide realistic default photo if not added, or require at least 1 photo
                    _uiState.value = s.copy(stepError = "Lampirkan minimal 1 foto bukti kondisi di lokasi")
                    return
                }
            }
            3 -> {
                if (s.address.isBlank()) {
                    _uiState.value = s.copy(stepError = "Lokasi dan alamat kejadian wajib diisi")
                    return
                }
            }
            4 -> {
                // Severity is always selected by default
            }
            5 -> {
                if (s.title.trim().length < 5) {
                    _uiState.value = s.copy(stepError = "Judul laporan minimal 5 karakter")
                    return
                }
                if (s.description.trim().length < 15) {
                    _uiState.value = s.copy(stepError = "Deskripsi kejadian minimal 15 karakter agar mudah dipahami petugas")
                    return
                }
            }
            6 -> {
                // Submit from Review Step
                submitReport()
                return
            }
        }

        val next = (s.currentStep + 1).coerceAtMost(7)
        _uiState.value = s.copy(currentStep = next, stepError = null)
        saveCurrentDraft()
    }

    fun prevStep() {
        val s = _uiState.value
        val prev = (s.currentStep - 1).coerceAtLeast(1)
        _uiState.value = s.copy(currentStep = prev, stepError = null)
        saveCurrentDraft()
    }

    fun saveCurrentDraft() {
        val s = _uiState.value
        viewModelScope.launch {
            hazardReportRepository.saveDraft(
                DraftReport(
                    category = s.category,
                    photoUris = s.photoUrls,
                    latitude = s.latitude,
                    longitude = s.longitude,
                    address = s.address,
                    landmark = s.landmark,
                    severity = s.severity,
                    title = s.title,
                    description = s.description,
                    currentStep = s.currentStep
                )
            )
        }
    }

    fun discardDraft() {
        viewModelScope.launch {
            hazardReportRepository.clearDraft()
            _uiState.value = CreateReportUiState()
        }
    }

    fun submitReport() {
        val s = _uiState.value
        _uiState.value = s.copy(isLoading = true, stepError = null)

        viewModelScope.launch {
            val user = authRepository.getCurrentUser().firstOrNull()
            val report = HazardReport(
                id = "",
                title = s.title.trim(),
                description = s.description.trim(),
                category = s.category ?: HazardCategory.LAINNYA,
                severity = s.severity,
                status = ReportStatus.DIKIRIM,
                latitude = s.latitude,
                longitude = s.longitude,
                address = s.address,
                landmark = s.landmark.ifBlank { null },
                photoUrls = s.photoUrls,
                createdAt = "",
                updatedAt = "",
                userId = user?.id ?: "user_guest",
                userName = user?.name ?: "Warga Pelapor",
                userPhone = user?.phoneNumber
            )

            val result = hazardReportRepository.createReport(report)
            result.fold(
                onSuccess = { created ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        currentStep = 7,
                        isSubmitted = true,
                        createdReport = created
                    )
                },
                onFailure = { err ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        stepError = err.localizedMessage ?: "Gagal mengirim laporan. Silakan coba lagi."
                    )
                }
            )
        }
    }
}
