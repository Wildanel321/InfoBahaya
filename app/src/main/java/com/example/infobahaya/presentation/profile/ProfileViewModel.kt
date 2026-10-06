package com.example.infobahaya.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.infobahaya.core.di.ServiceLocator
import com.example.infobahaya.domain.model.User
import com.example.infobahaya.domain.model.UserRole
import com.example.infobahaya.domain.repository.AuthRepository
import com.example.infobahaya.domain.repository.HazardReportRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

data class ProfileUiState(
    val user: User? = null,
    val totalUserReports: Int = 0,
    val resolvedReportsCount: Int = 0,
    val isLogoutDialogVisible: Boolean = false,
    val isRoleSwitching: Boolean = false,
    val isLoggedOut: Boolean = false
)

class ProfileViewModel(
    private val authRepository: AuthRepository = ServiceLocator.provideAuthRepository(),
    private val hazardReportRepository: HazardReportRepository = ServiceLocator.provideHazardReportRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadUserProfile()
    }

    private fun loadUserProfile() {
        viewModelScope.launch {
            combine(
                authRepository.getCurrentUser(),
                hazardReportRepository.getAllReports()
            ) { user, reports ->
                val myReports = reports.filter { it.userId == (user?.id ?: "") }
                val myResolved = myReports.count { it.status.name == "SELESAI" }
                ProfileUiState(
                    user = user,
                    totalUserReports = if (user?.role == UserRole.MODERATOR) 42 else myReports.size.coerceAtLeast(user?.totalReportsCount ?: 0),
                    resolvedReportsCount = if (user?.role == UserRole.MODERATOR) 38 else myResolved.coerceAtLeast(user?.resolvedReportsCount ?: 0)
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    fun toggleRole() {
        val currentUser = _uiState.value.user ?: return
        viewModelScope.launch {
            val nextRole = if (currentUser.role == UserRole.MODERATOR) UserRole.CITIZEN else UserRole.MODERATOR
            val nextEmail = if (nextRole == UserRole.MODERATOR) "moderator.budi@infobahaya.go.id" else "rian.pratama@gmail.com"
            authRepository.login(nextEmail, "password123")
        }
    }

    fun showLogoutDialog(show: Boolean) {
        _uiState.value = _uiState.value.copy(isLogoutDialogVisible = show)
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
            _uiState.value = _uiState.value.copy(isLoggedOut = true, isLogoutDialogVisible = false)
        }
    }
}
