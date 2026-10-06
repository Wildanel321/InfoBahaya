package com.example.infobahaya.network.model

import kotlinx.serialization.Serializable

@Serializable
data class ApiResponse<T>(
    val success: Boolean,
    val message: String,
    val data: T? = null,
    val errorCode: String? = null
)

@Serializable
data class LoginRequestDto(
    val email: String,
    val passwordHash: String
)

@Serializable
data class RegisterRequestDto(
    val name: String,
    val email: String,
    val passwordHash: String,
    val phone: String,
    val nik: String? = null
)

@Serializable
data class ReportDto(
    val id: String,
    val title: String,
    val description: String,
    val categoryId: String,
    val severityLevel: Int,
    val statusName: String,
    val latitude: Double,
    val longitude: Double,
    val address: String,
    val landmark: String? = null,
    val photoUrls: List<String> = emptyList(),
    val createdAt: String,
    val userId: String,
    val userName: String
)
