package com.example.infobahaya.database.entity

data class UserEntity(
    val id: String,
    val name: String,
    val email: String,
    val phoneNumber: String,
    val role: String,
    val token: String?,
    val address: String?,
    val nik: String?
)

data class ReportEntity(
    val id: String,
    val title: String,
    val description: String,
    val categoryId: String,
    val severityLevel: Int,
    val statusName: String,
    val latitude: Double,
    val longitude: Double,
    val address: String,
    val landmark: String?,
    val photoUrlsJson: String,
    val createdAt: String,
    val updatedAt: String,
    val userId: String,
    val userName: String,
    val verifiedBy: String?,
    val moderatorNote: String?,
    val assignedDepartment: String?,
    val isPriority: Boolean,
    val isSynced: Boolean,
    val upvotesCount: Int
)

data class DraftReportEntity(
    val id: String = "primary_draft",
    val categoryId: String?,
    val photoUrisJson: String,
    val latitude: Double?,
    val longitude: Double?,
    val address: String,
    val landmark: String,
    val severityLevel: Int,
    val title: String,
    val description: String,
    val currentStep: Int,
    val lastSavedAt: String
)

data class NotificationEntity(
    val id: String,
    val title: String,
    val message: String,
    val categoryName: String,
    val timestamp: String,
    val isRead: Boolean,
    val reportId: String?
)
