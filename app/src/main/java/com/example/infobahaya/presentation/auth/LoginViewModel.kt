package com.example.infobahaya.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.infobahaya.core.di.ServiceLocator
import com.example.infobahaya.core.util.ValidationUtils
import com.example.infobahaya.domain.model.User
import com.example.infobahaya.domain.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class LoginUiState(
    val email: String = "",
    val emailError: String? = null,
    val password: String = "",
    val passwordError: String? = null,
    val isPasswordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val generalErrorMessage: String? = null,
    val isSuccess: Boolean = false,
    val loggedInUser: User? = null
)

class LoginViewModel(
    private val authRepository: AuthRepository = ServiceLocator.provideAuthRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState = _uiState.asStateFlow()

    fun onEmailChange(newEmail: String) {
        _uiState.value = _uiState.value.copy(
            email = newEmail,
            emailError = null,
            generalErrorMessage = null
        )
    }

    fun onPasswordChange(newPassword: String) {
        _uiState.value = _uiState.value.copy(
            password = newPassword,
            passwordError = null,
            generalErrorMessage = null
        )
    }

    fun togglePasswordVisibility() {
        _uiState.value = _uiState.value.copy(
            isPasswordVisible = !_uiState.value.isPasswordVisible
        )
    }

    fun fillDemoAccount(isModerator: Boolean) {
        if (isModerator) {
            _uiState.value = _uiState.value.copy(
                email = "moderator.budi@infobahaya.go.id",
                password = "password123",
                emailError = null,
                passwordError = null,
                generalErrorMessage = null
            )
        } else {
            _uiState.value = _uiState.value.copy(
                email = "rian.pratama@gmail.com",
                password = "password123",
                emailError = null,
                passwordError = null,
                generalErrorMessage = null
            )
        }
    }

    fun login() {
        val currentState = _uiState.value
        var hasError = false
        var emailErr: String? = null
        var passErr: String? = null

        val emailTrimmed = currentState.email.trim()
        val passTrimmed = currentState.password.trim()

        if (emailTrimmed.isBlank()) {
            emailErr = "Email wajib diisi"
            hasError = true
        } else if (!ValidationUtils.isValidEmail(emailTrimmed)) {
            emailErr = "Format email tidak valid"
            hasError = true
        }

        if (passTrimmed.isBlank()) {
            passErr = "Password wajib diisi"
            hasError = true
        } else if (passTrimmed.length < 6) {
            passErr = "Password minimal 6 karakter"
            hasError = true
        }

        if (hasError) {
            _uiState.value = currentState.copy(
                emailError = emailErr,
                passwordError = passErr
            )
            return
        }

        _uiState.value = currentState.copy(isLoading = true, generalErrorMessage = null)

        viewModelScope.launch {
            val result = authRepository.login(emailTrimmed, passTrimmed)
            result.fold(
                onSuccess = { user ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isSuccess = true,
                        loggedInUser = user
                    )
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        generalErrorMessage = error.localizedMessage ?: "Login gagal. Silakan periksa kembali email dan password Anda."
                    )
                }
            )
        }
    }
}
