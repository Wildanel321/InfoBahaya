package com.example.infobahaya.presentation.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.infobahaya.core.theme.InfoBahayaThemeTokens
import com.example.infobahaya.core.theme.PrimaryBlue
import com.example.infobahaya.presentation.components.InfoBahayaButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit
) {
    var notifEmergency by remember { mutableStateOf(true) }
    var notifReportUpdate by remember { mutableStateOf(true) }
    var notifNearbyHazard by remember { mutableStateOf(true) }
    var alertRadiusKm by remember { mutableFloatStateOf(5.0f) }
    var highAccuracyGps by remember { mutableStateOf(true) }
    var autoSyncWifiOnly by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pengaturan Aplikasi", style = MaterialTheme.typography.titleLarge) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Notification Preferences
            Text(
                text = "Preferensi Pemberitahuan & Peringatan",
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(8.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = InfoBahayaThemeTokens.shapes.card,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    SettingSwitchRow(
                        title = "Peringatan Bahaya Darurat (Level 4)",
                        subtitle = "Bunyi sirine instan & broadcast push alert",
                        checked = notifEmergency,
                        onCheckedChange = { notifEmergency = it }
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    SettingSwitchRow(
                        title = "Update Status Tindak Lanjut Laporan",
                        subtitle = "Notifikasi saat dinas memverifikasi atau menyelesaikan laporan Anda",
                        checked = notifReportUpdate,
                        onCheckedChange = { notifReportUpdate = it }
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    SettingSwitchRow(
                        title = "Pemberitahuan Bahaya Sekitar",
                        subtitle = "Informasi bahaya pohon tumbang, kabel putus, jalan rusak di sekitar rute",
                        checked = notifNearbyHazard,
                        onCheckedChange = { notifNearbyHazard = it }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Radius Deteksi Bahaya Sekitar: ${alertRadiusKm.toInt()} km",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Slider(
                        value = alertRadiusKm,
                        onValueChange = { alertRadiusKm = it },
                        valueRange = 1f..15f,
                        steps = 13
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Permissions & Data
            Text(
                text = "Lokasi & Sinkronisasi Offline",
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(8.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = InfoBahayaThemeTokens.shapes.card,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    SettingSwitchRow(
                        title = "Mode GPS Akurasi Tinggi",
                        subtitle = "Menggunakan satelit GLONASS/GPS untuk titik koordinat presisi",
                        checked = highAccuracyGps,
                        onCheckedChange = { highAccuracyGps = it }
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    SettingSwitchRow(
                        title = "Sinkronisasi Hanya Lewat Wi-Fi",
                        subtitle = "Menghemat kuota seluler saat mengunggah foto laporan resolusi tinggi",
                        checked = autoSyncWifiOnly,
                        onCheckedChange = { autoSyncWifiOnly = it }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            InfoBahayaButton(
                text = "Simpan Preferensi",
                onClick = onNavigateBack
            )
        }
    }
}

@Composable
private fun SettingSwitchRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}
