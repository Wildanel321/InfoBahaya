package com.example.infobahaya.presentation.emergency

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.infobahaya.core.di.ServiceLocator
import com.example.infobahaya.domain.model.EmergencyContact
import com.example.infobahaya.domain.model.HazardReport
import com.example.infobahaya.domain.model.HazardSeverity
import com.example.infobahaya.domain.repository.EmergencyRepository
import com.example.infobahaya.domain.repository.HazardReportRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class EmergencyUiState(
    val contacts: List<EmergencyContact> = emptyList(),
    val nearbyEmergencyReports: List<HazardReport> = emptyList(),
    val isSosTriggered: Boolean = false,
    val sosFeedbackMessage: String? = null,
    val isTriggeringSos: Boolean = false,
    val selectedSafetyGuideIndex: Int? = null
)

class EmergencyViewModel(
    private val emergencyRepository: EmergencyRepository = ServiceLocator.provideEmergencyRepository(),
    private val hazardReportRepository: HazardReportRepository = ServiceLocator.provideHazardReportRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(EmergencyUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            emergencyRepository.getEmergencyContacts().collect { list ->
                _uiState.value = _uiState.value.copy(contacts = list)
            }
        }
        viewModelScope.launch {
            hazardReportRepository.getAllReports().collect { all ->
                val emergencies = all.filter { it.severity == HazardSeverity.DARURAT || it.severity == HazardSeverity.TINGGI }
                _uiState.value = _uiState.value.copy(nearbyEmergencyReports = emergencies)
            }
        }
    }

    fun triggerSos() {
        _uiState.value = _uiState.value.copy(isTriggeringSos = true)
        viewModelScope.launch {
            val result = emergencyRepository.triggerEmergencyAlert(
                latitude = -6.2088,
                longitude = 106.8456,
                emergencyType = "SOS_BEACON",
                description = "Sinyal bahaya darurat warga dari aplikasi InfoBahaya"
            )
            result.fold(
                onSuccess = { msg ->
                    _uiState.value = _uiState.value.copy(
                        isTriggeringSos = false,
                        isSosTriggered = true,
                        sosFeedbackMessage = msg
                    )
                },
                onFailure = { err ->
                    _uiState.value = _uiState.value.copy(
                        isTriggeringSos = false,
                        sosFeedbackMessage = err.localizedMessage ?: "Gagal memancarkan sinyal darurat."
                    )
                }
            )
        }
    }

    fun dismissSosDialog() {
        _uiState.value = _uiState.value.copy(isSosTriggered = false)
    }

    fun selectSafetyGuide(index: Int?) {
        _uiState.value = _uiState.value.copy(selectedSafetyGuideIndex = index)
    }
}
