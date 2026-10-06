package com.example.infobahaya.domain.model

data class TimelineEvent(
    val id: String,
    val reportId: String,
    val status: ReportStatus,
    val title: String,
    val description: String,
    val timestamp: String,
    val actorName: String,
    val actorRole: String,
    val evidencePhotoUrl: String? = null,
    val note: String? = null
)
