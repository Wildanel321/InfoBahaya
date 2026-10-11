package com.example.infobahaya.presentation.moderator

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.infobahaya.core.di.ServiceLocator
import com.example.infobahaya.domain.model.AnalyticsData
import com.example.infobahaya.domain.model.HazardCategory
import com.example.infobahaya.domain.model.HazardReport
import com.example.infobahaya.domain.model.HazardSeverity
import com.example.infobahaya.domain.model.ReportStatus
import com.example.infobahaya.domain.repository.AnalyticsRepository
import com.example.infobahaya.domain.repository.HazardReportRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class ModeratorFilterTab(val title: String) {
    ALL("Semua"),
    PENDING("Perlu Validasi"),
    IN_PROGRESS("Diproses"),
    RESOLVED("Selesai"),
    REJECTED("Ditolak")
}

data class ModeratorUiState(
    val rawReports: List<HazardReport> = emptyList(),
    val filteredReportsQueue: List<HazardReport> = emptyList(),
    val analytics: AnalyticsData? = null,
    val selectedReportForAction: HazardReport? = null,
    val selectedTab: Int = 0, // 0: Antrian Masuk & Validasi, 1: Analisis & Grafik
    val activeFilterTab: ModeratorFilterTab = ModeratorFilterTab.ALL,
    val selectedCategory: HazardCategory? = null,
    val searchQuery: String = "",
    val isLoading: Boolean = false,
    val feedbackMessage: String? = null,
    val pendingCount: Int = 0,
    val inProgressCount: Int = 0,
    val resolvedCount: Int = 0,
    val rejectedCount: Int = 0
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
            _uiState.update { it.copy(isLoading = true) }
            hazardReportRepository.getAllReports().collect { list ->
                _uiState.update { current ->
                    val pending = list.count { it.status == ReportStatus.DIKIRIM || it.status == ReportStatus.MENUNGGU_VERIFIKASI }
                    val inProgress = list.count { it.status == ReportStatus.DIPROSES || it.status == ReportStatus.DITERUSKAN || it.status == ReportStatus.DIVERIFIKASI }
                    val resolved = list.count { it.status == ReportStatus.SELESAI }
                    val rejected = list.count { it.status == ReportStatus.DITOLAK }

                    val filtered = applyFilter(
                        list = list,
                        filterTab = current.activeFilterTab,
                        searchQuery = current.searchQuery,
                        category = current.selectedCategory
                    )

                    current.copy(
                        rawReports = list,
                        filteredReportsQueue = filtered,
                        pendingCount = pending,
                        inProgressCount = inProgress,
                        resolvedCount = resolved,
                        rejectedCount = rejected,
                        isLoading = false
                    )
                }
            }
        }

        viewModelScope.launch {
            analyticsRepository.getAnalyticsData().collect { data ->
                _uiState.update { it.copy(analytics = data) }
            }
        }
    }

    private fun applyFilter(
        list: List<HazardReport>,
        filterTab: ModeratorFilterTab,
        searchQuery: String,
        category: HazardCategory?
    ): List<HazardReport> {
        return list.filter { report ->
            val matchTab = when (filterTab) {
                ModeratorFilterTab.ALL -> true
                ModeratorFilterTab.PENDING -> report.status == ReportStatus.DIKIRIM || report.status == ReportStatus.MENUNGGU_VERIFIKASI
                ModeratorFilterTab.IN_PROGRESS -> report.status == ReportStatus.DIPROSES || report.status == ReportStatus.DITERUSKAN || report.status == ReportStatus.DIVERIFIKASI
                ModeratorFilterTab.RESOLVED -> report.status == ReportStatus.SELESAI
                ModeratorFilterTab.REJECTED -> report.status == ReportStatus.DITOLAK
            }

            val matchCategory = category == null || report.category == category

            val matchQuery = searchQuery.isBlank() ||
                    report.id.contains(searchQuery, ignoreCase = true) ||
                    report.title.contains(searchQuery, ignoreCase = true) ||
                    report.description.contains(searchQuery, ignoreCase = true) ||
                    report.address.contains(searchQuery, ignoreCase = true) ||
                    report.userName.contains(searchQuery, ignoreCase = true) ||
                    report.category.displayName.contains(searchQuery, ignoreCase = true)

            matchTab && matchCategory && matchQuery
        }
    }

    fun selectTab(tabIndex: Int) {
        _uiState.update { it.copy(selectedTab = tabIndex) }
    }

    fun setFilterTab(filterTab: ModeratorFilterTab) {
        _uiState.update { current ->
            val filtered = applyFilter(
                list = current.rawReports,
                filterTab = filterTab,
                searchQuery = current.searchQuery,
                category = current.selectedCategory
            )
            current.copy(activeFilterTab = filterTab, filteredReportsQueue = filtered)
        }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update { current ->
            val filtered = applyFilter(
                list = current.rawReports,
                filterTab = current.activeFilterTab,
                searchQuery = query,
                category = current.selectedCategory
            )
            current.copy(searchQuery = query, filteredReportsQueue = filtered)
        }
    }

    fun onCategorySelect(category: HazardCategory?) {
        _uiState.update { current ->
            val newCategory = if (current.selectedCategory == category) null else category
            val filtered = applyFilter(
                list = current.rawReports,
                filterTab = current.activeFilterTab,
                searchQuery = current.searchQuery,
                category = newCategory
            )
            current.copy(selectedCategory = newCategory, filteredReportsQueue = filtered)
        }
    }

    fun openActionDialog(report: HazardReport) {
        _uiState.update { it.copy(selectedReportForAction = report) }
    }

    fun closeActionDialog() {
        _uiState.update { it.copy(selectedReportForAction = null) }
    }

    fun quickApprove(report: HazardReport) {
        viewModelScope.launch {
            val result = hazardReportRepository.updateReportStatus(
                reportId = report.id,
                newStatus = ReportStatus.DIVERIFIKASI,
                moderatorNote = "Laporan diverifikasi valid oleh admin & masuk antrian prioritas.",
                assignedDepartment = when (report.category) {
                    HazardCategory.LISTRIK_KABEL -> "PLN UID Jakarta Raya (Yantek)"
                    HazardCategory.POHON_TUMBANG -> "Dinas Pertamanan dan Hutan Kota"
                    HazardCategory.BANJIR -> "Dinas Sumber Daya Air & BPBD"
                    HazardCategory.KEBAKARAN -> "Dinas Penanggulangan Kebakaran & Penyelamatan"
                    HazardCategory.KRIMINAL -> "Polres Metro / Polsek Setempat"
                    else -> "Suku Dinas Bina Marga"
                }
            )
            result.onSuccess {
                _uiState.update { it.copy(feedbackMessage = "Laporan ${report.id} berhasil DIVERIFIKASI!") }
            }
        }
    }

    fun quickReject(report: HazardReport, reason: String = "Informasi kurang spesifik / tidak valid.") {
        viewModelScope.launch {
            val result = hazardReportRepository.updateReportStatus(
                reportId = report.id,
                newStatus = ReportStatus.DITOLAK,
                moderatorNote = "Ditolak oleh moderator: $reason"
            )
            result.onSuccess {
                _uiState.update { it.copy(feedbackMessage = "Laporan ${report.id} telah DITOLAK.") }
            }
        }
    }

    fun submitModerationDecision(
        newStatus: ReportStatus,
        note: String,
        assignedDept: String?,
        newSeverity: HazardSeverity? = null
    ) {
        val report = _uiState.value.selectedReportForAction ?: return
        viewModelScope.launch {
            val result = hazardReportRepository.updateReportStatus(
                reportId = report.id,
                newStatus = newStatus,
                moderatorNote = note,
                assignedDepartment = assignedDept,
                newSeverity = newSeverity
            )
            result.onSuccess {
                closeActionDialog()
                _uiState.update {
                    it.copy(feedbackMessage = "Keputusan validasi ${report.id} tersimpan: ${newStatus.displayName}")
                }
            }
        }
    }

    fun dismissFeedback() {
        _uiState.update { it.copy(feedbackMessage = null) }
    }
}
