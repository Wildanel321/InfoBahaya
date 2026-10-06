package com.example.infobahaya.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.infobahaya.core.di.ServiceLocator
import com.example.infobahaya.domain.repository.AuthRepository
import com.example.infobahaya.navigation.AppRoute
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class SplashDestination {
    data object Loading : SplashDestination()
    data class NavigateTo(val route: String) : SplashDestination()
}

class SplashViewModel(
    private val authRepository: AuthRepository = ServiceLocator.provideAuthRepository()
) : ViewModel() {

    private val _destination = MutableStateFlow<SplashDestination>(SplashDestination.Loading)
    val destination = _destination.asStateFlow()

    init {
        checkSessionAndNavigate()
    }

    private fun checkSessionAndNavigate() {
        viewModelScope.launch {
            delay(1200) // Smooth splash animation duration
            val hasSession = authRepository.hasActiveSession()
            val hasOnboarded = authRepository.hasCompletedOnboarding()

            val route = when {
                !hasOnboarded -> AppRoute.Onboarding.route
                hasSession -> AppRoute.Main.route
                else -> AppRoute.Login.route
            }
            _destination.value = SplashDestination.NavigateTo(route)
        }
    }
}
