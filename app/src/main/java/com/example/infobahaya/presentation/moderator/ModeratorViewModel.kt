package com.example.infobahaya.presentation.moderator

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.infobahaya.core.di.ServiceLocator
import com.example.infobahaya.domain.model.AnalyticsData
import com.example.infobahaya.domain.model.HazardReport
import com.example.infobahaya.domain.model.ReportStatus
import com.example.infobahaya.domain.repository.AnalyticsRepository
import com.example.infobahaya.domain.repository.HazardReportRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ModeratorUiState(
    val reportsQueue: List<HazardReport> = emptyList(),
    val analytics: AnalyticsData? = null,
    val selectedReportForAction: HazardReport? = null,
    val selectedTab: Int = 0, // 0: Antrian Moderasi, 1: Analisis & Grafik
    val isLoading: Boolean = false,
    val statusFilter: ReportStatus? = null,
    val searchQuery: String = ""
)

class ModeratorViewModel(
    private val hazardReportRepository: HazardReportRepository = ServiceLocator.provideHazardReportRepository(),
    private val analyticsRepository: AnalyticsRepository = ServiceLocator.provideAnalyticsRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(ModeratorUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            hazardReportRepository.getAllReports().collect { list ->
                _uiState.value = _uiState.value.copy(
                    reportsQueue = list,
                    isLoading = false
                )
            }
        }
        viewModelScope.launch {
            analyticsRepository.getAnalyticsData().collect { data ->
                _uiState.value = _uiState.value.copy(analytics = data)
            }
        }
    }

    fun selectTab(tabIndex: Int) {
        _uiState.value = _uiState.value.copy(selectedTab = tabIndex)
    }

    fun openActionDialog(report: HazardReport) {
        _uiState.value = _uiState.value.copy(selectedReportForAction = report)
    }

    fun closeActionDialog() {
        _uiState.value = _uiState.value.copy(selectedReportForAction = null)
    }

    fun submitModerationDecision(
        newStatus: ReportStatus,
        note: String,
        assignedDept: String?
    ) {
        val report = _uiState.value.selectedReportForAction ?: return
        viewModelScope.launch {
            hazardReportRepository.updateReportStatus(
                reportId = report.id,
                newStatus = newStatus,
                moderatorNote = note,
                assignedDepartment = assignedDept
            )
            closeActionDialog()
        }
    }
}
