package com.example.infobahaya.presentation.tracking

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.infobahaya.core.di.ServiceLocator
import com.example.infobahaya.domain.model.HazardReport
import com.example.infobahaya.domain.repository.HazardReportRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ReportDetailUiState(
    val report: HazardReport? = null,
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)

class ReportDetailViewModel(
    private val hazardReportRepository: HazardReportRepository = ServiceLocator.provideHazardReportRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReportDetailUiState())
    val uiState = _uiState.asStateFlow()

    fun loadReport(reportId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            hazardReportRepository.getReportById(reportId).collect { found ->
                if (found != null) {
                    _uiState.value = ReportDetailUiState(report = found, isLoading = false)
                } else {
                    _uiState.value = ReportDetailUiState(
                        isLoading = false,
                        errorMessage = "Laporan dengan ID '$reportId' tidak ditemukan."
                    )
                }
            }
        }
    }

    fun toggleUpvote() {
        val current = _uiState.value.report ?: return
        viewModelScope.launch {
            val result = hazardReportRepository.toggleUpvote(current.id)
            result.onSuccess { updated ->
                _uiState.value = _uiState.value.copy(report = updated)
            }
        }
    }
}
