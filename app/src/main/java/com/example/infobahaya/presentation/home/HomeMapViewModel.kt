package com.example.infobahaya.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.infobahaya.core.di.ServiceLocator
import com.example.infobahaya.domain.model.HazardCategory
import com.example.infobahaya.domain.model.HazardReport
import com.example.infobahaya.domain.model.HazardSeverity
import com.example.infobahaya.domain.model.ReportStatus
import com.example.infobahaya.domain.repository.AuthRepository
import com.example.infobahaya.domain.repository.HazardReportRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

data class HomeMapUiState(
    val reports: List<HazardReport> = emptyList(),
    val filteredReports: List<HazardReport> = emptyList(),
    val selectedReport: HazardReport? = null,
    val selectedCategory: HazardCategory? = null,
    val selectedSeverity: HazardSeverity? = null,
    val selectedStatus: ReportStatus? = null,
    val searchQuery: String = "",
    val userLatitude: Double = -6.200000,
    val userLongitude: Double = 106.816666,
    val userAddress: String = "Menteng, Jakarta Pusat",
    val mapLayerType: MapLayerType = MapLayerType.STANDARD,
    val isFilterDialogVisible: Boolean = false,
    val isLegendVisible: Boolean = false,
    val isLoading: Boolean = false,
    val offlinePendingCount: Int = 0,
    val isOffline: Boolean = false,
    val activeEmergenciesCount: Int = 0
)

enum class MapLayerType(val label: String) {
    STANDARD("Standar"),
    SATELLITE("Satelit"),
    HEATMAP("Peta Panas (Risiko)")
}

class HomeMapViewModel(
    private val hazardReportRepository: HazardReportRepository = ServiceLocator.provideHazardReportRepository(),
    private val authRepository: AuthRepository = ServiceLocator.provideAuthRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeMapUiState())
    val uiState = _uiState.asStateFlow()

    init {
        observeReports()
        observeOfflineState()
    }

    private fun observeReports() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            hazardReportRepository.getAllReports().collect { allReports ->
                val emergencies = allReports.count { it.severity == HazardSeverity.DARURAT && it.status != ReportStatus.SELESAI }
                _uiState.value = _uiState.value.copy(
                    reports = allReports,
                    isLoading = false,
                    activeEmergenciesCount = emergencies
                )
                applyFilters()
            }
        }
    }

    private fun observeOfflineState() {
        viewModelScope.launch {
            combine(
                hazardReportRepository.getOfflinePendingCount(),
                hazardReportRepository.isOfflineMode()
            ) { count, offline ->
                Pair(count, offline)
            }.collect { (count, offline) ->
                _uiState.value = _uiState.value.copy(
                    offlinePendingCount = count,
                    isOffline = offline
                )
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
        applyFilters()
    }

    fun onSelectCategory(category: HazardCategory?) {
        val newCategory = if (_uiState.value.selectedCategory == category) null else category
        _uiState.value = _uiState.value.copy(selectedCategory = newCategory)
        applyFilters()
    }

    fun onSelectSeverity(severity: HazardSeverity?) {
        val newSeverity = if (_uiState.value.selectedSeverity == severity) null else severity
        _uiState.value = _uiState.value.copy(selectedSeverity = newSeverity)
        applyFilters()
    }

    fun onSelectStatus(status: ReportStatus?) {
        val newStatus = if (_uiState.value.selectedStatus == status) null else status
        _uiState.value = _uiState.value.copy(selectedStatus = newStatus)
        applyFilters()
    }

    fun onSelectReportMarker(report: HazardReport?) {
        _uiState.value = _uiState.value.copy(selectedReport = report)
    }

    fun toggleMapLayer(layerType: MapLayerType) {
        _uiState.value = _uiState.value.copy(mapLayerType = layerType)
    }

    fun toggleFilterDialog(show: Boolean) {
        _uiState.value = _uiState.value.copy(isFilterDialogVisible = show)
    }

    fun toggleLegend(show: Boolean) {
        _uiState.value = _uiState.value.copy(isLegendVisible = show)
    }

    fun toggleUpvote(reportId: String) {
        viewModelScope.launch {
            val result = hazardReportRepository.toggleUpvote(reportId)
            result.onSuccess { updatedReport ->
                if (_uiState.value.selectedReport?.id == reportId) {
                    _uiState.value = _uiState.value.copy(selectedReport = updatedReport)
                }
            }
        }
    }

    fun toggleOfflineMode() {
        viewModelScope.launch {
            val current = _uiState.value.isOffline
            hazardReportRepository.setOfflineMode(!current)
        }
    }

    fun resetFilters() {
        _uiState.value = _uiState.value.copy(
            selectedCategory = null,
            selectedSeverity = null,
            selectedStatus = null,
            searchQuery = ""
        )
        applyFilters()
    }

    private fun applyFilters() {
        val s = _uiState.value
        val filtered = s.reports.filter { r ->
            val matchCat = s.selectedCategory == null || r.category == s.selectedCategory
            val matchSev = s.selectedSeverity == null || r.severity == s.selectedSeverity
            val matchStat = s.selectedStatus == null || r.status == s.selectedStatus
            val matchQuery = s.searchQuery.isBlank() ||
                    r.title.contains(s.searchQuery, ignoreCase = true) ||
                    r.description.contains(s.searchQuery, ignoreCase = true) ||
                    r.address.contains(s.searchQuery, ignoreCase = true) ||
                    r.category.displayName.contains(s.searchQuery, ignoreCase = true)
            matchCat && matchSev && matchStat && matchQuery
        }
        _uiState.value = s.copy(filteredReports = filtered)
    }
}
