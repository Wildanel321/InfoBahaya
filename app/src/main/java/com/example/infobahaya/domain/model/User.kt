package com.example.infobahaya.domain.model

enum class UserRole(val displayName: String) {
    CITIZEN("Warga / Pelapor"),
    MODERATOR("Petugas Moderator"),
    ADMIN("Administrator Sistem")
}

data class User(
    val id: String,
    val name: String,
    val email: String,
    val phoneNumber: String,
    val role: UserRole = UserRole.CITIZEN,
    val avatarUrl: String? = null,
    val token: String? = null,
    val address: String? = "Jakarta Pusat, DKI Jakarta",
    val nik: String? = null,
    val totalReportsCount: Int = 0,
    val resolvedReportsCount: Int = 0,
    val isVerifiedUser: Boolean = true
)
