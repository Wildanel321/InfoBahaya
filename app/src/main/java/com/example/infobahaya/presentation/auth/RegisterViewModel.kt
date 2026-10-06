package com.example.infobahaya.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.infobahaya.core.di.ServiceLocator
import com.example.infobahaya.domain.model.User
import com.example.infobahaya.domain.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class RegisterUiState(
    val name: String = "",
    val nameError: String? = null,
    val email: String = "",
    val emailError: String? = null,
    val phone: String = "",
    val phoneError: String? = null,
    val nik: String = "",
    val nikError: String? = null,
    val password: String = "",
    val passwordError: String? = null,
    val confirmPassword: String = "",
    val confirmPasswordError: String? = null,
    val isPasswordVisible: Boolean = false,
    val agreedToTerms: Boolean = false,
    val termsError: String? = null,
    val isLoading: Boolean = false,
    val generalErrorMessage: String? = null,
    val isSuccess: Boolean = false,
    val createdUser: User? = null
)

class RegisterViewModel(
    private val authRepository: AuthRepository = ServiceLocator.provideAuthRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState = _uiState.asStateFlow()

    fun onNameChange(value: String) {
        _uiState.value = _uiState.value.copy(name = value, nameError = null)
    }

    fun onEmailChange(value: String) {
        _uiState.value = _uiState.value.copy(email = value, emailError = null)
    }

    fun onPhoneChange(value: String) {
        _uiState.value = _uiState.value.copy(phone = value, phoneError = null)
    }

    fun onNikChange(value: String) {
        if (value.length <= 16) {
            _uiState.value = _uiState.value.copy(nik = value, nikError = null)
        }
    }

    fun onPasswordChange(value: String) {
        _uiState.value = _uiState.value.copy(password = value, passwordError = null)
    }

    fun onConfirmPasswordChange(value: String) {
        _uiState.value = _uiState.value.copy(confirmPassword = value, confirmPasswordError = null)
    }

    fun togglePasswordVisibility() {
        _uiState.value = _uiState.value.copy(isPasswordVisible = !_uiState.value.isPasswordVisible)
    }

    fun toggleTermsAgreement(checked: Boolean) {
        _uiState.value = _uiState.value.copy(agreedToTerms = checked, termsError = null)
    }

    fun register() {
        val s = _uiState.value
        var hasError = false
        var nameErr: String? = null
        var emailErr: String? = null
        var phoneErr: String? = null
        var nikErr: String? = null
        var passErr: String? = null
        var confirmErr: String? = null
        var termsErr: String? = null

        val nameTrimmed = s.name.trim()
        val emailTrimmed = s.email.trim()
        val phoneTrimmed = s.phone.trim()
        val nikTrimmed = s.nik.trim()
        val passTrimmed = s.password.trim()
        val confirmTrimmed = s.confirmPassword.trim()

        if (nameTrimmed.isBlank()) {
            nameErr = "Nama lengkap wajib diisi"
            hasError = true
        }

        if (emailTrimmed.isBlank()) {
            emailErr = "Email wajib diisi"
            hasError = true
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(emailTrimmed).matches()) {
            emailErr = "Format email tidak valid"
            hasError = true
        }

        if (phoneTrimmed.isBlank()) {
            phoneErr = "Nomor HP wajib diisi"
            hasError = true
        } else if (phoneTrimmed.length < 10) {
            phoneErr = "Nomor HP minimal 10 digit"
            hasError = true
        }

        if (nikTrimmed.isNotBlank() && nikTrimmed.length != 16) {
            nikErr = "NIK harus tepat 16 digit"
            hasError = true
        }

        if (passTrimmed.isBlank()) {
            passErr = "Password wajib diisi"
            hasError = true
        } else if (passTrimmed.length < 6) {
            passErr = "Password minimal 6 karakter"
            hasError = true
        }

        if (confirmTrimmed != passTrimmed) {
            confirmErr = "Konfirmasi password tidak cocok"
            hasError = true
        }

        if (!s.agreedToTerms) {
            termsErr = "Anda harus menyetujui syarat & ketentuan layanan"
            hasError = true
        }

        if (hasError) {
            _uiState.value = s.copy(
                nameError = nameErr,
                emailError = emailErr,
                phoneError = phoneErr,
                nikError = nikErr,
                passwordError = passErr,
                confirmPasswordError = confirmErr,
                termsError = termsErr
            )
            return
        }

        _uiState.value = s.copy(isLoading = true, generalErrorMessage = null)

        viewModelScope.launch {
            val result = authRepository.register(
                name = nameTrimmed,
                email = emailTrimmed,
                password = passTrimmed,
                phone = phoneTrimmed,
                nik = nikTrimmed
            )

            result.fold(
                onSuccess = { user ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isSuccess = true,
                        createdUser = user
                    )
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        generalErrorMessage = error.localizedMessage ?: "Pendaftaran gagal. Silakan coba lagi."
                    )
                }
            )
        }
    }
}
