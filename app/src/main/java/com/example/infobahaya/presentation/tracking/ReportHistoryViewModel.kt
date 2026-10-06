package com.example.infobahaya.presentation.tracking

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.infobahaya.core.di.ServiceLocator
import com.example.infobahaya.domain.model.HazardCategory
import com.example.infobahaya.domain.model.HazardReport
import com.example.infobahaya.domain.model.ReportStatus
import com.example.infobahaya.domain.repository.AuthRepository
import com.example.infobahaya.domain.repository.HazardReportRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

data class ReportHistoryUiState(
    val allReports: List<HazardReport> = emptyList(),
    val filteredReports: List<HazardReport> = emptyList(),
    val selectedStatusFilter: ReportStatus? = null,
    val selectedCategoryFilter: HazardCategory? = null,
    val searchQuery: String = "",
    val isLoading: Boolean = false,
    val isMyReportsOnly: Boolean = false,
    val currentUserId: String = "user_101"
)

class ReportHistoryViewModel(
    private val hazardReportRepository: HazardReportRepository = ServiceLocator.provideHazardReportRepository(),
    private val authRepository: AuthRepository = ServiceLocator.provideAuthRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReportHistoryUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadReports()
    }

    private fun loadReports() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val user = authRepository.getCurrentUser().firstOrNull()
            val userId = user?.id ?: "user_101"

            hazardReportRepository.getAllReports().collect { list ->
                _uiState.value = _uiState.value.copy(
                    allReports = list,
                    currentUserId = userId,
                    isLoading = false
                )
                applyFilters()
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
        applyFilters()
    }

    fun onSelectStatusFilter(status: ReportStatus?) {
        val newStatus = if (_uiState.value.selectedStatusFilter == status) null else status
        _uiState.value = _uiState.value.copy(selectedStatusFilter = newStatus)
        applyFilters()
    }

    fun onSelectCategoryFilter(cat: HazardCategory?) {
        val newCat = if (_uiState.value.selectedCategoryFilter == cat) null else cat
        _uiState.value = _uiState.value.copy(selectedCategoryFilter = newCat)
        applyFilters()
    }

    fun toggleMyReportsOnly(myOnly: Boolean) {
        _uiState.value = _uiState.value.copy(isMyReportsOnly = myOnly)
        applyFilters()
    }

    private fun applyFilters() {
        val s = _uiState.value
        val filtered = s.allReports.filter { r ->
            val matchUser = !s.isMyReportsOnly || r.userId == s.currentUserId
            val matchStatus = s.selectedStatusFilter == null || r.status == s.selectedStatusFilter
            val matchCategory = s.selectedCategoryFilter == null || r.category == s.selectedCategoryFilter
            val matchQuery = s.searchQuery.isBlank() ||
                    r.title.contains(s.searchQuery, ignoreCase = true) ||
                    r.description.contains(s.searchQuery, ignoreCase = true) ||
                    r.address.contains(s.searchQuery, ignoreCase = true) ||
                    r.id.contains(s.searchQuery, ignoreCase = true)
            matchUser && matchStatus && matchCategory && matchQuery
        }
        _uiState.value = s.copy(filteredReports = filtered)
    }
}
