package com.example.infobahaya.presentation.moderator

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PriorityHigh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.infobahaya.core.theme.InfoBahayaThemeTokens
import com.example.infobahaya.core.theme.PrimaryBlue
import com.example.infobahaya.core.theme.SeverityEmergency
import com.example.infobahaya.domain.model.HazardCategory
import com.example.infobahaya.domain.model.HazardReport
import com.example.infobahaya.domain.model.ReportStatus
import com.example.infobahaya.presentation.components.EmptyState
import com.example.infobahaya.presentation.components.LoadingState
import com.example.infobahaya.presentation.components.SeverityBadge
import com.example.infobahaya.presentation.components.StatusBadge
import com.example.infobahaya.presentation.moderator.components.CategoryDistributionChart
import com.example.infobahaya.presentation.moderator.components.KpiSummaryRow
import com.example.infobahaya.presentation.moderator.components.ModerationActionDialog
import com.example.infobahaya.presentation.moderator.components.MonthlyTrendLineChart

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModeratorDashboardScreen(
    onNavigateBack: () -> Unit,
    onNavigateToReportDetail: (String) -> Unit,
    viewModel: ModeratorViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.feedbackMessage) {
        uiState.feedbackMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.dismissFeedback()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Admin Dashboard",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF10B981))
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Validasi Laporan Publik Aktif",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                },
                actions = {
                    Box(
                        modifier = Modifier
                            .padding(end = 12.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFEDE9FE))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AdminPanelSettings,
                                contentDescription = null,
                                tint = Color(0xFF7C3AED),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Budi (Validator)",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = Color(0xFF7C3AED)
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Main Tabs Row (Antrian Validasi vs Analitik)
            TabRow(
                selectedTabIndex = uiState.selectedTab,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = PrimaryBlue,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
            ) {
                Tab(
                    selected = uiState.selectedTab == 0,
                    onClick = { viewModel.selectTab(0) },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Assignment,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Validasi Masuk (${uiState.rawReports.size})",
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                )
                Tab(
                    selected = uiState.selectedTab == 1,
                    onClick = { viewModel.selectTab(1) },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Analytics,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Analitik & SLA",
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                )
            }

            if (uiState.isLoading) {
                LoadingState(message = "Memuat data validasi laporan...")
            } else if (uiState.selectedTab == 0) {
                // Tab 0: Moderation & Validation Queue
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Spacer(modifier = Modifier.height(4.dp))
                        // Quick KPI Status Filter Cards (Clickable!)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            FilterCounterCard(
                                label = "Perlu Validasi",
                                count = uiState.pendingCount,
                                color = Color(0xFFF59E0B),
                                isSelected = uiState.activeFilterTab == ModeratorFilterTab.PENDING,
                                onClick = {
                                    viewModel.setFilterTab(
                                        if (uiState.activeFilterTab == ModeratorFilterTab.PENDING) ModeratorFilterTab.ALL
                                        else ModeratorFilterTab.PENDING
                                    )
                                },
                                modifier = Modifier.weight(1f)
                            )
                            FilterCounterCard(
                                label = "Diproses",
                                count = uiState.inProgressCount,
                                color = PrimaryBlue,
                                isSelected = uiState.activeFilterTab == ModeratorFilterTab.IN_PROGRESS,
                                onClick = {
                                    viewModel.setFilterTab(
                                        if (uiState.activeFilterTab == ModeratorFilterTab.IN_PROGRESS) ModeratorFilterTab.ALL
                                        else ModeratorFilterTab.IN_PROGRESS
                                    )
                                },
                                modifier = Modifier.weight(1f)
                            )
                            FilterCounterCard(
                                label = "Selesai",
                                count = uiState.resolvedCount,
                                color = Color(0xFF10B981),
                                isSelected = uiState.activeFilterTab == ModeratorFilterTab.RESOLVED,
                                onClick = {
                                    viewModel.setFilterTab(
                                        if (uiState.activeFilterTab == ModeratorFilterTab.RESOLVED) ModeratorFilterTab.ALL
                                        else ModeratorFilterTab.RESOLVED
                                    )
                                },
                                modifier = Modifier.weight(1f)
                            )
                            FilterCounterCard(
                                label = "Ditolak",
                                count = uiState.rejectedCount,
                                color = Color(0xFFEF4444),
                                isSelected = uiState.activeFilterTab == ModeratorFilterTab.REJECTED,
                                onClick = {
                                    viewModel.setFilterTab(
                                        if (uiState.activeFilterTab == ModeratorFilterTab.REJECTED) ModeratorFilterTab.ALL
                                        else ModeratorFilterTab.REJECTED
                                    )
                                },
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Search Bar
                        OutlinedTextField(
                            value = uiState.searchQuery,
                            onValueChange = { viewModel.onSearchQueryChange(it) },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("Cari ID laporan, lokasi, atau pelapor...") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = null,
                                    tint = PrimaryBlue
                                )
                            },
                            trailingIcon = {
                                if (uiState.searchQuery.isNotBlank()) {
                                    IconButton(onClick = { viewModel.onSearchQueryChange("") }) {
                                        Icon(imageVector = Icons.Default.Close, contentDescription = "Hapus")
                                    }
                                }
                            },
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true,
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = MaterialTheme.colorScheme.surface,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                                focusedIndicatorColor = PrimaryBlue,
                                unfocusedIndicatorColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                            )
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Category Filter Chips
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            item {
                                FilterChipPill(
                                    label = "Semua Kategori",
                                    isSelected = uiState.selectedCategory == null,
                                    onClick = { viewModel.onCategorySelect(null) }
                                )
                            }
                            items(HazardCategory.entries) { cat ->
                                FilterChipPill(
                                    label = cat.displayName,
                                    isSelected = uiState.selectedCategory == cat,
                                    onClick = { viewModel.onCategorySelect(cat) }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                    }

                    if (uiState.filteredReportsQueue.isEmpty()) {
                        item {
                            EmptyState(
                                title = "Tidak Ada Laporan Ditemukan",
                                message = "Tidak ada laporan yang sesuai dengan filter atau kata kunci pencarian saat ini."
                            )
                        }
                    } else {
                        items(uiState.filteredReportsQueue, key = { it.id }) { report ->
                            AdminModerationItemCard(
                                report = report,
                                onQuickApprove = { viewModel.quickApprove(report) },
                                onQuickReject = { viewModel.quickReject(report) },
                                onModerateClick = { viewModel.openActionDialog(report) },
                                onDetailClick = { onNavigateToReportDetail(report.id) }
                            )
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(32.dp))
                    }
                }
            } else {
                // Tab 1: Municipal Analytics Dashboard
                val analytics = uiState.analytics
                if (analytics != null) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        KpiSummaryRow(analytics = analytics)
                        MonthlyTrendLineChart(trends = analytics.monthlyTrends)
                        CategoryDistributionChart(analytics = analytics)
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                } else {
                    LoadingState(message = "Menghitung analitik penanganan...")
                }
            }
        }

        // Moderation Action Decision Dialog
        if (uiState.selectedReportForAction != null) {
            ModerationActionDialog(
                report = uiState.selectedReportForAction!!,
                onConfirm = { newStatus, note, dept, newSeverity ->
                    viewModel.submitModerationDecision(newStatus, note, dept, newSeverity)
                },
                onDismiss = { viewModel.closeActionDialog() }
            )
        }
    }
}

@Composable
private fun FilterCounterCard(
    label: String,
    count: Int,
    color: Color,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .clip(InfoBahayaThemeTokens.shapes.small)
            .clickable { onClick() },
        shape = InfoBahayaThemeTokens.shapes.small,
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) color.copy(alpha = 0.22f) else color.copy(alpha = 0.08f)
        ),
        border = if (isSelected) androidx.compose.foundation.BorderStroke(1.5.dp, color) else null
    ) {
        Column(
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "$count",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = color
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = color,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun FilterChipPill(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(if (isSelected) PrimaryBlue else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f))
            .clickable { onClick() }
            .padding(horizontal = 10.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal),
            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun AdminModerationItemCard(
    report: HazardReport,
    onQuickApprove: () -> Unit,
    onQuickReject: () -> Unit,
    onModerateClick: () -> Unit,
    onDetailClick: () -> Unit
) {
    val isPending = report.status == ReportStatus.DIKIRIM || report.status == ReportStatus.MENUNGGU_VERIFIKASI

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = InfoBahayaThemeTokens.shapes.card,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Pending Verification Urgent Banner
            if (isPending) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFFEF3C7))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.PriorityHigh,
                            contentDescription = null,
                            tint = Color(0xFFD97706),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Menunggu Validasi Petugas",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = Color(0xFFB45309)
                        )
                    }
                }
            }

            // Header (ID + Badges)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = report.id,
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = PrimaryBlue
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "• ${report.createdAt}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    StatusBadge(status = report.status)
                    SeverityBadge(severity = report.severity)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Title
            Text(
                text = report.title,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Description
            Text(
                text = report.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Reporter & Location Info
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${report.userName} • ",
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = PrimaryBlue,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(3.dp))
                Text(
                    text = report.address,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            if (!report.assignedDepartment.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Disposisi: ${report.assignedDepartment}",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                    color = PrimaryBlue
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action Buttons Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Detail Button
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(InfoBahayaThemeTokens.shapes.button)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .clickable { onDetailClick() }
                        .padding(vertical = 9.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Rincian",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                // If Pending, show Quick Validate + Reject
                if (isPending) {
                    Box(
                        modifier = Modifier
                            .weight(1.3f)
                            .clip(InfoBahayaThemeTokens.shapes.button)
                            .background(Color(0xFF10B981))
                            .clickable { onQuickApprove() }
                            .padding(vertical = 9.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Validasi Cepat",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                        }
                    }
                }

                // Decision / Follow-up Dialog Button
                Box(
                    modifier = Modifier
                        .weight(1.4f)
                        .clip(InfoBahayaThemeTokens.shapes.button)
                        .background(PrimaryBlue)
                        .clickable { onModerateClick() }
                        .padding(vertical = 9.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isPending) "Disposisi" else "Ubah Status",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}
