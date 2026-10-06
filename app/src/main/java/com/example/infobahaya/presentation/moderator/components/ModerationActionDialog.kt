package com.example.infobahaya.presentation.moderator.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.infobahaya.core.theme.InfoBahayaThemeTokens
import com.example.infobahaya.core.theme.PrimaryBlue
import com.example.infobahaya.domain.model.HazardReport
import com.example.infobahaya.domain.model.ReportStatus
import com.example.infobahaya.presentation.components.InfoBahayaButton
import com.example.infobahaya.presentation.components.InfoBahayaOutlinedButton
import com.example.infobahaya.presentation.components.InfoBahayaTextField

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ModerationActionDialog(
    report: HazardReport,
    onConfirm: (ReportStatus, String, String?) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedStatus by remember { mutableStateOf(ReportStatus.DIVERIFIKASI) }
    var note by remember { mutableStateOf("Laporan telah diverifikasi dan siap ditindaklanjuti.") }
    var selectedDept by remember {
        mutableStateOf(
            when (report.category.name) {
                "LISTRIK_KABEL" -> "PLN UID Jakarta Raya (Tim Yantek)"
                "POHON_TUMBANG" -> "Dinas Pertamanan dan Hutan Kota DKI"
                "BANJIR" -> "Dinas Sumber Daya Air & BPBD DKI"
                "KEBAKARAN" -> "Dinas Pemadam Kebakaran & Penyelamatan (Damkar)"
                "KRIMINAL" -> "Polres Metro / Polsek Setempat"
                else -> "Suku Dinas Bina Marga (PU)"
            }
        )
    }

    val availableDepts = listOf(
        "Suku Dinas Bina Marga (PU)",
        "PLN UID Jakarta Raya (Tim Yantek)",
        "Dinas Pertamanan dan Hutan Kota DKI",
        "Dinas Sumber Daya Air & BPBD DKI",
        "Dinas Pemadam Kebakaran (Damkar)",
        "Dinas Perhubungan (Dishub)",
        "Polres Metro / Polsek Setempat"
    )

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Moderasi Laporan",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = report.id,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = PrimaryBlue
                        )
                    }

                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Tutup")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Action / New Status Selector
                Text(
                    text = "Pilih Keputusan Status Baru",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(8.dp))

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val statusOptions = listOf(
                        ReportStatus.DIVERIFIKASI,
                        ReportStatus.DITERUSKAN,
                        ReportStatus.DIPROSES,
                        ReportStatus.SELESAI,
                        ReportStatus.DITOLAK
                    )

                    statusOptions.forEach { st ->
                        val isSelected = selectedStatus == st
                        Box(
                            modifier = Modifier
                                .clip(InfoBahayaThemeTokens.shapes.badge)
                                .background(if (isSelected) st.color else st.containerColor)
                                .clickable {
                                    selectedStatus = st
                                    note = when (st) {
                                        ReportStatus.DIVERIFIKASI -> "Laporan dinyatakan valid dan masuk prioritas penanganan."
                                        ReportStatus.DITERUSKAN -> "Diteruskan ke $selectedDept untuk eksekusi lapangan."
                                        ReportStatus.DIPROSES -> "Petugas teknis telah berada di lokasi untuk tindakan perbaikan."
                                        ReportStatus.SELESAI -> "Bahaya telah selesai ditangani secara tuntas dan aman."
                                        ReportStatus.DITOLAK -> "Laporan ditolak: Informasi tidak lengkap atau duplikasi."
                                        else -> ""
                                    }
                                }
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = st.displayName,
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = if (isSelected) Color.White else st.contentColor
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Assign Department
                Text(
                    text = "Disposisi / Instansi Penanggung Jawab",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(8.dp))

                availableDepts.forEach { dept ->
                    val isSelected = selectedDept == dept
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 6.dp)
                            .clip(InfoBahayaThemeTokens.shapes.small)
                            .background(
                                if (isSelected) PrimaryBlue.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                            )
                            .clickable {
                                selectedDept = dept
                                if (selectedStatus == ReportStatus.DITERUSKAN) {
                                    note = "Diteruskan ke $dept untuk eksekusi lapangan."
                                }
                            }
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = dept,
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            ),
                            color = if (isSelected) PrimaryBlue else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Moderator Note Input
                InfoBahayaTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = "Catatan Resmi Petugas / Alasan",
                    singleLine = false,
                    minLines = 3,
                    maxLines = 5
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    InfoBahayaOutlinedButton(
                        text = "Batal",
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    )
                    InfoBahayaButton(
                        text = "Simpan Keputusan",
                        onClick = { onConfirm(selectedStatus, note, selectedDept) },
                        modifier = Modifier.weight(1.5f)
                    )
                }
            }
        }
    }
}
