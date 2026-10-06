package com.example.infobahaya.domain.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Engineering
import androidx.compose.material.icons.filled.Flood
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.NaturePeople
import androidx.compose.material.icons.filled.ReportProblem
import androidx.compose.material.icons.filled.Security
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

enum class HazardCategory(
    val id: String,
    val displayName: String,
    val description: String,
    val icon: ImageVector,
    val tintColor: Color
) {
    JALAN_RUSAK(
        id = "jalan_rusak",
        displayName = "Jalan Rusak",
        description = "Lubang, aspal amblas, jalan retak berbahaya bagi pengendara",
        icon = Icons.Default.Engineering,
        tintColor = Color(0xFFF59E0B)
    ),
    POHON_TUMBANG(
        id = "pohon_tumbang",
        displayName = "Pohon Tumbang",
        description = "Pohon roboh atau dahan lapuk menghalangi jalan / menimpa kabel",
        icon = Icons.Default.NaturePeople,
        tintColor = Color(0xFF10B981)
    ),
    LISTRIK_KABEL(
        id = "listrik_kabel",
        displayName = "Listrik / Kabel",
        description = "Kabel putus menjuntai, tiang miring, korsleting, trafo meledak",
        icon = Icons.Default.ElectricBolt,
        tintColor = Color(0xFFEAB308)
    ),
    BANJIR(
        id = "banjir",
        displayName = "Banjir",
        description = "Genangan air tinggi, luapan sungai, saluran drainase tersumbat",
        icon = Icons.Default.Flood,
        tintColor = Color(0xFF0284C7)
    ),
    LONGSOR(
        id = "longsor",
        displayName = "Longsor",
        description = "Tanah longsor, tebing runtuh menutupi akses jalan pemukiman",
        icon = Icons.Default.ReportProblem,
        tintColor = Color(0xFFD97706)
    ),
    KEBAKARAN(
        id = "kebakaran",
        displayName = "Kebakaran",
        description = "Kebakaran gedung, lahan terbuka, sampah liar yang tidak terkendali",
        icon = Icons.Default.LocalFireDepartment,
        tintColor = Color(0xFFEF4444)
    ),
    KRIMINAL(
        id = "kriminal",
        displayName = "Kriminal",
        description = "Tawuran, pembegalan, pencurian, ancaman keamanan publik",
        icon = Icons.Default.Security,
        tintColor = Color(0xFF8B5CF6)
    ),
    LAINNYA(
        id = "lainnya",
        displayName = "Lainnya",
        description = "Bahaya fasilitas umum lainnya yang butuh penanganan segera",
        icon = Icons.Default.MoreHoriz,
        tintColor = Color(0xFF64748B)
    );

    companion object {
        fun fromId(id: String): HazardCategory {
            return entries.firstOrNull { it.id.equals(id, ignoreCase = true) } ?: LAINNYA
        }
    }
}
