package com.example.infobahaya.domain.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dangerous
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PriorityHigh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.infobahaya.core.theme.SeverityEmergency
import com.example.infobahaya.core.theme.SeverityEmergencyContainer
import com.example.infobahaya.core.theme.SeverityEmergencyText
import com.example.infobahaya.core.theme.SeverityHigh
import com.example.infobahaya.core.theme.SeverityHighContainer
import com.example.infobahaya.core.theme.SeverityHighText
import com.example.infobahaya.core.theme.SeverityLow
import com.example.infobahaya.core.theme.SeverityLowContainer
import com.example.infobahaya.core.theme.SeverityLowText
import com.example.infobahaya.core.theme.SeverityMedium
import com.example.infobahaya.core.theme.SeverityMediumContainer
import com.example.infobahaya.core.theme.SeverityMediumText

enum class HazardSeverity(
    val level: Int,
    val displayName: String,
    val description: String,
    val slaText: String,
    val icon: ImageVector,
    val color: Color,
    val containerColor: Color,
    val contentColor: Color
) {
    RENDAH(
        level = 1,
        displayName = "Rendah",
        description = "Tidak mengancam nyawa langsung, berdampak minor pada kelancaran",
        slaText = "Target penanganan 3-7 hari",
        icon = Icons.Default.Info,
        color = SeverityLow,
        containerColor = SeverityLowContainer,
        contentColor = SeverityLowText
    ),
    SEDANG(
        level = 2,
        displayName = "Sedang",
        description = "Berdampak signifikan, dapat menyebabkan kecelakaan jika dibiarkan",
        slaText = "Target penanganan 1-2 hari",
        icon = Icons.Default.Warning,
        color = SeverityMedium,
        containerColor = SeverityMediumContainer,
        contentColor = SeverityMediumText
    ),
    TINGGI(
        level = 3,
        displayName = "Tinggi",
        description = "Sangat berbahaya, potensi fatalitas atau kerusakan aset meluas",
        slaText = "Target respon < 12 jam",
        icon = Icons.Default.PriorityHigh,
        color = SeverityHigh,
        containerColor = SeverityHighContainer,
        contentColor = SeverityHighText
    ),
    DARURAT(
        level = 4,
        displayName = "Darurat",
        description = "Ancaman keselamatan jiwa langsung / bencana aktif yang berlangsung",
        slaText = "Respon instan segera (< 1 jam)",
        icon = Icons.Default.Dangerous,
        color = SeverityEmergency,
        containerColor = SeverityEmergencyContainer,
        contentColor = SeverityEmergencyText
    );

    companion object {
        fun fromLevel(level: Int): HazardSeverity {
            return entries.firstOrNull { it.level == level } ?: SEDANG
        }

        fun fromName(name: String): HazardSeverity {
            return entries.firstOrNull { it.name.equals(name, ignoreCase = true) || it.displayName.equals(name, ignoreCase = true) } ?: SEDANG
        }
    }
}
