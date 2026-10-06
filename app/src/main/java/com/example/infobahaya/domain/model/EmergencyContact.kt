package com.example.infobahaya.domain.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.LocalPolice
import androidx.compose.material.icons.filled.PhoneInTalk
import androidx.compose.material.icons.filled.Rescue
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

enum class EmergencyType(
    val title: String,
    val icon: ImageVector,
    val accentColor: Color
) {
    DARURAT_112("Layanan Tanggap Darurat Terpadu", Icons.Default.PhoneInTalk, Color(0xFFEF4444)),
    POLISI_110("Kepolisian RI (Keamanan & Kriminal)", Icons.Default.LocalPolice, Color(0xFF3B82F6)),
    DAMKAR_113("Pemadam Kebakaran & Penyelamatan", Icons.Default.Rescue, Color(0xFFF97316)),
    AMBULANS_119("Ambulans & Gawat Darurat Medis", Icons.Default.LocalHospital, Color(0xFF10B981)),
    SAR_BASARNAS("SAR / Evakuasi Bencana Alam", Icons.Default.SupportAgent, Color(0xFFEAB308)),
    PLN_GANGGUAN("PLN (Gangguan & Bahaya Listrik)", Icons.Default.ElectricBolt, Color(0xFF6366F1))
}

data class EmergencyContact(
    val id: String,
    val name: String,
    val number: String,
    val type: EmergencyType,
    val description: String,
    val is24Hours: Boolean = true,
    val isTollFree: Boolean = true
)
