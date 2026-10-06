package com.example.infobahaya.domain.repository

import com.example.infobahaya.domain.model.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    fun getCurrentUser(): Flow<User?>
    fun hasActiveSession(): Boolean
    fun hasCompletedOnboarding(): Boolean
    fun setOnboardingCompleted(completed: Boolean)
    suspend fun login(email: String, password: String): Result<User>
    suspend fun register(name: String, email: String, password: String, phone: String, nik: String): Result<User>
    suspend fun forgotPassword(email: String): Result<String>
    suspend fun updateProfile(name: String, phone: String, address: String): Result<User>
    suspend fun logout(): Result<Unit>
}
