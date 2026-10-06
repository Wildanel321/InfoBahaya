package com.example.infobahaya.domain.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Dangerous
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Verified
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

enum class NotificationCategory(
    val displayName: String,
    val icon: ImageVector,
    val color: Color
) {
    REPORT_UPDATE(
        displayName = "Report Update",
        icon = Icons.Default.NotificationsActive,
        color = Color(0xFF2563EB)
    ),
    NEARBY_HAZARD(
        displayName = "Nearby Hazard",
        icon = Icons.Default.Campaign,
        color = Color(0xFFF97316)
    ),
    EMERGENCY(
        displayName = "Emergency",
        icon = Icons.Default.Dangerous,
        color = Color(0xFFEF4444)
    ),
    SYSTEM(
        displayName = "System",
        icon = Icons.Default.Info,
        color = Color(0xFF64748B)
    ),
    VERIFICATION(
        displayName = "Verification",
        icon = Icons.Default.Verified,
        color = Color(0xFF10B981)
    )
}

data class NotificationItem(
    val id: String,
    val title: String,
    val message: String,
    val category: NotificationCategory,
    val timestamp: String,
    val isRead: Boolean = false,
    val reportId: String? = null,
    val actionUrl: String? = null
)
