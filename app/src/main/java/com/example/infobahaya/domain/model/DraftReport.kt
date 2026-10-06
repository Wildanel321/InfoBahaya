package com.example.infobahaya.domain.model

data class DraftReport(
    val id: String = "draft_current",
    val category: HazardCategory? = null,
    val photoUris: List<String> = emptyList(),
    val latitude: Double? = null,
    val longitude: Double? = null,
    val address: String = "",
    val landmark: String = "",
    val severity: HazardSeverity = HazardSeverity.SEDANG,
    val title: String = "",
    val description: String = "",
    val currentStep: Int = 1,
    val lastSavedAt: String = ""
)
