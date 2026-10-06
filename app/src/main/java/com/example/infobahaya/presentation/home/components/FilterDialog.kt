package com.example.infobahaya.presentation.home.components

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
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.infobahaya.core.theme.InfoBahayaThemeTokens
import com.example.infobahaya.core.theme.PrimaryBlue
import com.example.infobahaya.domain.model.HazardCategory
import com.example.infobahaya.domain.model.HazardSeverity
import com.example.infobahaya.domain.model.ReportStatus
import com.example.infobahaya.presentation.components.InfoBahayaButton
import com.example.infobahaya.presentation.components.InfoBahayaOutlinedButton

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FilterDialog(
    selectedCategory: HazardCategory?,
    selectedSeverity: HazardSeverity?,
    selectedStatus: ReportStatus?,
    onSelectCategory: (HazardCategory?) -> Unit,
    onSelectSeverity: (HazardSeverity?) -> Unit,
    onSelectStatus: (ReportStatus?) -> Unit,
    onResetFilters: () -> Unit,
    onDismiss: () -> Unit
) {
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
                    Text(
                        text = "Filter Laporan Bahaya",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Tutup")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Severity Filter Section
                Text(
                    text = "Tingkat Bahaya (Severity)",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    HazardSeverity.entries.forEach { severity ->
                        val isSelected = selectedSeverity == severity
                        Box(
                            modifier = Modifier
                                .clip(InfoBahayaThemeTokens.shapes.badge)
                                .background(
                                    if (isSelected) severity.color else severity.containerColor
                                )
                                .clickable { onSelectSeverity(severity) }
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = severity.displayName.uppercase(),
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = if (isSelected) Color.White else severity.contentColor
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Status Filter Section
                Text(
                    text = "Status Penanganan",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ReportStatus.entries.filter { it != ReportStatus.DITOLAK }.forEach { status ->
                        val isSelected = selectedStatus == status
                        Box(
                            modifier = Modifier
                                .clip(InfoBahayaThemeTokens.shapes.badge)
                                .background(
                                    if (isSelected) PrimaryBlue else MaterialTheme.colorScheme.surfaceVariant
                                )
                                .clickable { onSelectStatus(status) }
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = status.displayName,
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    InfoBahayaOutlinedButton(
                        text = "Reset",
                        onClick = onResetFilters,
                        modifier = Modifier.weight(1f)
                    )
                    InfoBahayaButton(
                        text = "Terapkan",
                        onClick = onDismiss,
                        modifier = Modifier.weight(1.4f)
                    )
                }
            }
        }
    }
}
