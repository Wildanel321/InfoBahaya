package com.example.infobahaya.data.repository

import com.example.infobahaya.domain.model.User
import com.example.infobahaya.domain.model.UserRole
import com.example.infobahaya.domain.repository.AuthRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class MockAuthRepository : AuthRepository {

    private val _currentUser = MutableStateFlow<User?>(
        User(
            id = "user_101",
            name = "Rian Pratama",
            email = "rian.pratama@gmail.com",
            phoneNumber = "081234567890",
            role = UserRole.CITIZEN,
            token = "jwt_token_sample_session_101",
            address = "Jl. Sudirman No. 45, Jakarta Pusat",
            nik = "3171012304950001",
            totalReportsCount = 6,
            resolvedReportsCount = 4
        )
    )

    private var onboardingCompleted = true

    override fun getCurrentUser(): Flow<User?> = _currentUser.asStateFlow()

    override fun hasActiveSession(): Boolean = _currentUser.value != null

    override fun hasCompletedOnboarding(): Boolean = onboardingCompleted

    override fun setOnboardingCompleted(completed: Boolean) {
        onboardingCompleted = completed
    }

    override suspend fun login(email: String, password: String): Result<User> {
        delay(600) // Realistic network delay
        val cleanEmail = email.trim()
        val cleanPass = password.trim()

        if (cleanEmail.isBlank() || cleanPass.isBlank()) {
            return Result.failure(IllegalArgumentException("Email dan password wajib diisi"))
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(cleanEmail).matches()) {
            return Result.failure(IllegalArgumentException("Format email tidak valid (contoh: nama@domain.com)"))
        }
        if (cleanPass.length < 6) {
            return Result.failure(IllegalArgumentException("Password minimal 6 karakter"))
        }

        // Check for moderator account login
        val role = if (cleanEmail.contains("moderator", ignoreCase = true) || cleanEmail.contains("petugas", ignoreCase = true)) {
            UserRole.MODERATOR
        } else {
            UserRole.CITIZEN
        }

        val user = User(
            id = if (role == UserRole.MODERATOR) "mod_007" else "user_${System.currentTimeMillis() % 10000}",
            name = if (role == UserRole.MODERATOR) "Budi Setiawan (Moderator)" else "Rian Pratama",
            email = cleanEmail,
            phoneNumber = "081234567890",
            role = role,
            token = "jwt_token_session_${System.currentTimeMillis()}",
            address = "DKI Jakarta",
            nik = "3171012304950001",
            totalReportsCount = if (role == UserRole.MODERATOR) 42 else 6,
            resolvedReportsCount = if (role == UserRole.MODERATOR) 38 else 4
        )

        _currentUser.value = user
        return Result.success(user)
    }

    override suspend fun register(
        name: String,
        email: String,
        password: String,
        phone: String,
        nik: String
    ): Result<User> {
        delay(700)
        val cleanName = name.trim()
        val cleanEmail = email.trim()
        val cleanPass = password.trim()
        val cleanPhone = phone.trim()
        val cleanNik = nik.trim()

        if (cleanName.isBlank()) return Result.failure(IllegalArgumentException("Nama lengkap wajib diisi"))
        if (cleanEmail.isBlank()) return Result.failure(IllegalArgumentException("Email wajib diisi"))
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(cleanEmail).matches()) {
            return Result.failure(IllegalArgumentException("Format email tidak valid"))
        }
        if (cleanPhone.length < 10) return Result.failure(IllegalArgumentException("Nomor telepon minimal 10 digit"))
        if (cleanNik.isNotBlank() && cleanNik.length != 16) {
            return Result.failure(IllegalArgumentException("NIK harus berjumlah 16 digit"))
        }
        if (cleanPass.length < 6) return Result.failure(IllegalArgumentException("Password minimal 6 karakter"))

        val user = User(
            id = "user_${System.currentTimeMillis() % 10000}",
            name = cleanName,
            email = cleanEmail,
            phoneNumber = cleanPhone,
            role = UserRole.CITIZEN,
            token = "jwt_token_session_${System.currentTimeMillis()}",
            address = "Indonesia",
            nik = cleanNik.ifBlank { null },
            totalReportsCount = 0,
            resolvedReportsCount = 0
        )

        _currentUser.value = user
        return Result.success(user)
    }

    override suspend fun forgotPassword(email: String): Result<String> {
        delay(600)
        val cleanEmail = email.trim()
        if (cleanEmail.isBlank()) return Result.failure(IllegalArgumentException("Email wajib diisi"))
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(cleanEmail).matches()) {
            return Result.failure(IllegalArgumentException("Format email tidak valid"))
        }
        return Result.success("Link reset password telah dikirim ke $cleanEmail. Silakan periksa kotak masuk atau spam Anda.")
    }

    override suspend fun updateProfile(
        name: String,
        phone: String,
        address: String
    ): Result<User> {
        delay(400)
        val current = _currentUser.value ?: return Result.failure(IllegalStateException("User belum login"))
        val updated = current.copy(
            name = name.ifBlank { current.name },
            phoneNumber = phone.ifBlank { current.phoneNumber },
            address = address.ifBlank { current.address }
        )
        _currentUser.value = updated
        return Result.success(updated)
    }

    override suspend fun logout(): Result<Unit> {
        delay(200)
        _currentUser.value = null
        return Result.success(Unit)
    }
}
