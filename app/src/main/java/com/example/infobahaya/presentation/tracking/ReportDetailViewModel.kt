package com.example.infobahaya.presentation.tracking

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.infobahaya.core.di.ServiceLocator
import com.example.infobahaya.domain.model.HazardReport
import com.example.infobahaya.domain.model.HazardSeverity
import com.example.infobahaya.domain.model.ReportStatus
import com.example.infobahaya.domain.model.UserRole
import com.example.infobahaya.domain.repository.AuthRepository
import com.example.infobahaya.domain.repository.HazardReportRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ReportDetailUiState(
    val report: HazardReport? = null,
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val isUserModerator: Boolean = false,
    val isActionDialogVisible: Boolean = false,
    val feedbackMessage: String? = null
)

class ReportDetailViewModel(
    private val hazardReportRepository: HazardReportRepository = ServiceLocator.provideHazardReportRepository(),
    private val authRepository: AuthRepository = ServiceLocator.provideAuthRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReportDetailUiState())
    val uiState = _uiState.asStateFlow()

    init {
        observeUserRole()
    }

    private fun observeUserRole() {
        viewModelScope.launch {
            authRepository.getCurrentUser().collect { user ->
                _uiState.update { it.copy(isUserModerator = user?.role == UserRole.MODERATOR) }
            }
        }
    }

    fun loadReport(reportId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            hazardReportRepository.getReportById(reportId).collect { found ->
                if (found != null) {
                    _uiState.update { it.copy(report = found, isLoading = false, errorMessage = null) }
                } else {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "Laporan dengan ID '$reportId' tidak ditemukan."
                        )
                    }
                }
            }
        }
    }

    fun toggleUpvote() {
        val current = _uiState.value.report ?: return
        viewModelScope.launch {
            val result = hazardReportRepository.toggleUpvote(current.id)
            result.onSuccess { updated ->
                _uiState.update { it.copy(report = updated) }
            }
        }
    }

    fun openActionDialog() {
        _uiState.update { it.copy(isActionDialogVisible = true) }
    }

    fun closeActionDialog() {
        _uiState.update { it.copy(isActionDialogVisible = false) }
    }

    fun submitModerationDecision(
        newStatus: ReportStatus,
        note: String,
        assignedDept: String?,
        newSeverity: HazardSeverity?
    ) {
        val current = _uiState.value.report ?: return
        viewModelScope.launch {
            val result = hazardReportRepository.updateReportStatus(
                reportId = current.id,
                newStatus = newStatus,
                moderatorNote = note,
                assignedDepartment = assignedDept,
                newSeverity = newSeverity
            )
            result.onSuccess { updated ->
                _uiState.update {
                    it.copy(
                        report = updated,
                        isActionDialogVisible = false,
                        feedbackMessage = "Status laporan berhasil diperbarui menjadi ${newStatus.displayName}"
                    )
                }
            }
        }
    }

    fun dismissFeedback() {
        _uiState.update { it.copy(feedbackMessage = null) }
    }
}
