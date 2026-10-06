package com.example.infobahaya.presentation.emergency

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Dangerous
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Flood
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.infobahaya.core.theme.InfoBahayaThemeTokens
import com.example.infobahaya.core.theme.SeverityEmergency
import com.example.infobahaya.core.theme.SeverityEmergencyDark
import com.example.infobahaya.domain.model.EmergencyContact
import com.example.infobahaya.presentation.components.InfoBahayaButton
import com.example.infobahaya.presentation.components.SeverityBadge

data class SafetyGuideItem(
    val title: String,
    val icon: ImageVector,
    val steps: List<String>
)

@Composable
fun EmergencyScreen(
    onNavigateToReportDetail: (String) -> Unit,
    viewModel: EmergencyViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    val infiniteTransition = rememberInfiniteTransition(label = "sos_pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "sosScale"
    )

    fun makeCall(number: String) {
        val intent = Intent(Intent.ACTION_DIAL).apply {
            data = Uri.parse("tel:$number")
        }
        context.startActivity(intent)
    }

    val safetyGuides = listOf(
        SafetyGuideItem(
            title = "Kebakaran Gedung / Pemukiman",
            icon = Icons.Default.LocalFireDepartment,
            steps = listOf(
                "Jangan panik, segera bunyikan alarm kebakaran atau teriakkan peringatan ke sekitar.",
                "Gunakan tangga darurat, JANGAN sekali-kali menggunakan lift.",
                "Rangkak di bawah asap tebal dan tutup hidung/mulut dengan kain basah.",
                "Hubungi posko pemadam kebakaran 113 atau 112 segera."
            )
        ),
        SafetyGuideItem(
            title = "Kabel Listrik Putus / Korsleting",
            icon = Icons.Default.ElectricBolt,
            steps = listOf(
                "Jaga jarak aman minimal 10 meter dari kabel yang menyentuh tanah atau air.",
                "JANGAN menyentuh genangan air di sekitar tiang listrik yang roboh.",
                "Beri tanda peringatan darurat ke pengendara motor yang melintas.",
                "Hubungi PLN 123 atau 112 untuk pemutusan arus darurat."
            )
        ),
        SafetyGuideItem(
            title = "Banjir Bandang & Luapan Sungai",
            icon = Icons.Default.Flood,
            steps = listOf(
                "Matikan saklar MCB listrik utama di rumah segera sebelum air naik.",
                "Evakuasi dokumen penting dan anggota keluarga ke lantai atas atau posko aman.",
                "Hindari berjalan di arus air deras yang melebihi mata kaki.",
                "Hubungi SAR Basarnas 115 atau 112 jika butuh perahu karet evakuasi."
            )
        ),
        SafetyGuideItem(
            title = "Ancaman Kejahatan / Begal",
            icon = Icons.Default.Security,
            steps = listOf(
                "Segera menuju ke tempat ramai atau pos polisi / SPBU terdekat.",
                "Kunci pintu kendaraan dan bunyikan klakson panjang untuk menarik perhatian warga.",
                "Pancarkan sinyal SOS di InfoBahaya dan hubungi Call Center Polisi 110."
            )
        )
    )

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Pusat Tanggap Darurat",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = (-0.5).sp
                        ),
                        color = SeverityEmergency
                    )
                    Text(
                        text = "Akses cepat panggilan bantuan & panduan keselamatan resmi.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(InfoBahayaThemeTokens.shapes.badge)
                        .background(SeverityEmergency.copy(alpha = 0.15f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "24 JAM AKTIF",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = SeverityEmergency
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // BIG SOS PULSE BUTTON
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(SeverityEmergency, SeverityEmergencyDark)
                        )
                    )
                    .padding(20.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "PANCARKAN SINYAL SOS DARURAT",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 1.sp
                        ),
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Pulse Button
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .scale(pulseScale)
                            .clip(CircleShape)
                            .background(Color.White)
                            .clickable {
                                if (!uiState.isTriggeringSos) viewModel.triggerSos()
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        if (uiState.isTriggeringSos) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(36.dp),
                                color = SeverityEmergency,
                                strokeWidth = 3.dp
                            )
                        } else {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Default.Dangerous,
                                    contentDescription = "SOS",
                                    tint = SeverityEmergency,
                                    modifier = Modifier.size(44.dp)
                                )
                                Text(
                                    text = "SOS",
                                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.ExtraBold),
                                    color = SeverityEmergency
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Satu sentuhan menyiarkan koordinat GPS Anda ke command center 112 & warga terdekat",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.9f),
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Quick Call Emergency Contacts
            Text(
                text = "Nomor Darurat Resmi Indonesia (1-Tap Call)",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(10.dp))

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                uiState.contacts.forEach { contact ->
                    EmergencyContactCard(
                        contact = contact,
                        onCall = { makeCall(contact.number) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Active Nearby High Risks
            if (uiState.nearbyEmergencyReports.isNotEmpty()) {
                Text(
                    text = "Bahaya Darurat Aktif Sekitar Anda",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.height(10.dp))

                uiState.nearbyEmergencyReports.take(2).forEach { report ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                            .clip(InfoBahayaThemeTokens.shapes.card)
                            .clickable { onNavigateToReportDetail(report.id) },
                        shape = InfoBahayaThemeTokens.shapes.card,
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = null,
                                tint = SeverityEmergency,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = report.title,
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = report.address,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            SeverityBadge(severity = report.severity)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            // Safety Guides Accordion
            Text(
                text = "Panduan & Protokol Keselamatan Cepat",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(10.dp))

            safetyGuides.forEachIndexed { index, guide ->
                val isExpanded = uiState.selectedSafetyGuideIndex == index
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                        .clip(InfoBahayaThemeTokens.shapes.card)
                        .border(1.dp, MaterialTheme.colorScheme.outline, InfoBahayaThemeTokens.shapes.card)
                        .clickable {
                            viewModel.selectSafetyGuide(if (isExpanded) null else index)
                        },
                    shape = InfoBahayaThemeTokens.shapes.card,
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = guide.icon,
                                    contentDescription = null,
                                    tint = SeverityEmergency,
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = guide.title,
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            Icon(
                                imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        AnimatedVisibility(visible = isExpanded) {
                            Column(modifier = Modifier.padding(top = 12.dp)) {
                                guide.steps.forEachIndexed { stepIdx, stepText ->
                                    Row(
                                        modifier = Modifier.padding(vertical = 4.dp),
                                        verticalAlignment = Alignment.Top
                                    ) {
                                        Text(
                                            text = "${stepIdx + 1}. ",
                                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                            color = SeverityEmergency
                                        )
                                        Text(
                                            text = stepText,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }

        // SOS Feedback Dialog
        if (uiState.isSosTriggered) {
            AlertDialog(
                onDismissRequest = { viewModel.dismissSosDialog() },
                icon = {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = Color(0xFF10B981),
                        modifier = Modifier.size(44.dp)
                    )
                },
                title = { Text("Sinyal SOS Darurat Terpancar!") },
                text = {
                    Text(
                        text = uiState.sosFeedbackMessage.orEmpty(),
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center
                    )
                },
                confirmButton = {
                    InfoBahayaButton(
                        text = "Hubungi 112 Sekarang",
                        onClick = {
                            viewModel.dismissSosDialog()
                            makeCall("112")
                        }
                    )
                },
                dismissButton = {
                    TextButton(onClick = { viewModel.dismissSosDialog() }) {
                        Text(text = "Tutup")
                    }
                }
            )
        }
    }
}

@Composable
private fun EmergencyContactCard(
    contact: EmergencyContact,
    onCall: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(InfoBahayaThemeTokens.shapes.card)
            .clickable { onCall() },
        shape = InfoBahayaThemeTokens.shapes.card,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(contact.type.accentColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = contact.type.icon,
                    contentDescription = null,
                    tint = contact.type.accentColor,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = contact.name,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = contact.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Big Call Dial Badge
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(contact.type.accentColor)
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Call,
                        contentDescription = "Telepon",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = contact.number,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                        color = Color.White
                    )
                }
            }
        }
    }
}
