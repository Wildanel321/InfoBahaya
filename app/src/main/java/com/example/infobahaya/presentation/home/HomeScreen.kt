package com.example.infobahaya.presentation.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.infobahaya.core.theme.InfoBahayaThemeTokens
import com.example.infobahaya.core.theme.PrimaryBlue
import com.example.infobahaya.core.theme.SeverityEmergency
import com.example.infobahaya.presentation.components.InfoBahayaSearchBar
import com.example.infobahaya.presentation.home.components.CategoryFilterChips
import com.example.infobahaya.presentation.home.components.FilterDialog
import com.example.infobahaya.presentation.home.components.HazardMapCanvas
import com.example.infobahaya.presentation.home.components.HazardPreviewCard

@Composable
fun HomeScreen(
    onNavigateToReportDetail: (String) -> Unit,
    onNavigateToCreateReport: () -> Unit,
    onNavigateToEmergency: () -> Unit,
    viewModel: HomeMapViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToCreateReport,
                containerColor = PrimaryBlue,
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier
                    .padding(bottom = 8.dp)
                    .size(58.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Buat Laporan Baru",
                    modifier = Modifier.size(28.dp)
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // 1. Live Interactive Hazard Map Canvas
            HazardMapCanvas(
                reports = uiState.filteredReports,
                selectedReport = uiState.selectedReport,
                onSelectReport = { viewModel.onSelectReportMarker(it) },
                userLatitude = uiState.userLatitude,
                userLongitude = uiState.userLongitude,
                layerType = uiState.mapLayerType,
                modifier = Modifier.fillMaxSize()
            )

            // 2. Top Header Overlay (Search Bar + Category Filters + Emergency Banner)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .padding(top = 12.dp)
            ) {
                // Search Bar with Filter
                Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                    InfoBahayaSearchBar(
                        query = uiState.searchQuery,
                        onQueryChange = { viewModel.onSearchQueryChange(it) },
                        placeholder = "Cari bahaya di sekitar rute Anda...",
                        onFilterClick = { viewModel.toggleFilterDialog(true) },
                        isFilterActive = uiState.selectedCategory != null || uiState.selectedSeverity != null || uiState.selectedStatus != null
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Category Chips Scroll
                CategoryFilterChips(
                    selectedCategory = uiState.selectedCategory,
                    onSelectCategory = { viewModel.onSelectCategory(it) }
                )

                // Active Emergency Banner (if any)
                AnimatedVisibility(
                    visible = uiState.activeEmergenciesCount > 0,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(SeverityEmergency.copy(alpha = 0.92f))
                            .clickable { onNavigateToEmergency() }
                            .padding(horizontal = 14.dp, vertical = 10.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "${uiState.activeEmergenciesCount} Bahaya Darurat Aktif!",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                    color = Color.White
                                )
                                Text(
                                    text = "Klik untuk panduan keselamatan & akses cepat kontak 112",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.White.copy(alpha = 0.9f)
                                )
                            }
                        }
                    }
                }

                // Offline Notice Banner
                AnimatedVisibility(
                    visible = uiState.isOffline,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF334155))
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.CloudOff,
                                contentDescription = null,
                                tint = Color(0xFFFBBF24),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Mode Offline Aktif • Menunggu koneksi (${uiState.offlinePendingCount} draft)",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White
                            )
                        }
                    }
                }
            }

            // 3. Floating Map Controls (Right Side)
            Column(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Map Layer Switcher
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surface)
                        .shadow(4.dp, CircleShape)
                        .clickable {
                            val nextLayer = when (uiState.mapLayerType) {
                                MapLayerType.STANDARD -> MapLayerType.SATELLITE
                                MapLayerType.SATELLITE -> MapLayerType.HEATMAP
                                MapLayerType.HEATMAP -> MapLayerType.STANDARD
                            }
                            viewModel.toggleMapLayer(nextLayer)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Layers,
                        contentDescription = "Ganti Layer Peta",
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(22.dp)
                    )
                }

                // Center GPS Location
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surface)
                        .shadow(4.dp, CircleShape)
                        .clickable {
                            // Centering feedback
                            viewModel.onSelectReportMarker(null)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.MyLocation,
                        contentDescription = "Lokasi Saya",
                        tint = PrimaryBlue,
                        modifier = Modifier.size(22.dp)
                    )
                }

                // Offline Simulation Toggle Button
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(
                            if (uiState.isOffline) Color(0xFFF59E0B) else MaterialTheme.colorScheme.surface
                        )
                        .shadow(4.dp, CircleShape)
                        .clickable { viewModel.toggleOfflineMode() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CloudOff,
                        contentDescription = "Toggle Offline",
                        tint = if (uiState.isOffline) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // 4. Selected Hazard Preview Bottom Sheet Card
            HazardPreviewCard(
                report = uiState.selectedReport,
                onDismiss = { viewModel.onSelectReportMarker(null) },
                onViewDetail = { reportId -> onNavigateToReportDetail(reportId) },
                onToggleUpvote = { reportId -> viewModel.toggleUpvote(reportId) },
                modifier = Modifier.align(Alignment.BottomCenter)
            )

            // 5. Filter Dialog
            if (uiState.isFilterDialogVisible) {
                FilterDialog(
                    selectedCategory = uiState.selectedCategory,
                    selectedSeverity = uiState.selectedSeverity,
                    selectedStatus = uiState.selectedStatus,
                    onSelectCategory = { viewModel.onSelectCategory(it) },
                    onSelectSeverity = { viewModel.onSelectSeverity(it) },
                    onSelectStatus = { viewModel.onSelectStatus(it) },
                    onResetFilters = { viewModel.resetFilters() },
                    onDismiss = { viewModel.toggleFilterDialog(false) }
                )
            }
        }
    }
}
