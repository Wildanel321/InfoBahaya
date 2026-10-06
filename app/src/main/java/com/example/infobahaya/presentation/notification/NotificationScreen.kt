package com.example.infobahaya.presentation.notification

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.infobahaya.core.theme.InfoBahayaThemeTokens
import com.example.infobahaya.core.theme.PrimaryBlue
import com.example.infobahaya.domain.model.NotificationCategory
import com.example.infobahaya.domain.model.NotificationItem
import com.example.infobahaya.presentation.components.EmptyState
import com.example.infobahaya.presentation.components.LoadingState

@Composable
fun NotificationScreen(
    onNavigateToReportDetail: (String) -> Unit,
    viewModel: NotificationViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Top Header & Mark All as Read
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Pemberitahuan",
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = if (uiState.unreadCount > 0) "${uiState.unreadCount} pesan belum dibaca" else "Semua pesan telah dibaca",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                if (uiState.unreadCount > 0) {
                    TextButton(onClick = { viewModel.markAllAsRead() }) {
                        Icon(imageVector = Icons.Default.DoneAll, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "Tandai Semua", style = MaterialTheme.typography.labelMedium)
                    }
                }
            }

            // Category Filter Scroll
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val isAll = uiState.selectedCategory == null
                Box(
                    modifier = Modifier
                        .clip(InfoBahayaThemeTokens.shapes.badge)
                        .background(if (isAll) PrimaryBlue else MaterialTheme.colorScheme.surface)
                        .clickable { viewModel.onSelectCategory(null) }
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Semua (${uiState.notifications.size})",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = if (isAll) Color.White else MaterialTheme.colorScheme.onSurface
                    )
                }

                NotificationCategory.entries.forEach { cat ->
                    val isSelected = uiState.selectedCategory == cat
                    Box(
                        modifier = Modifier
                            .clip(InfoBahayaThemeTokens.shapes.badge)
                            .background(if (isSelected) cat.color else MaterialTheme.colorScheme.surface)
                            .clickable { viewModel.onSelectCategory(cat) }
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = cat.icon,
                                contentDescription = null,
                                tint = if (isSelected) Color.White else cat.color,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = cat.displayName,
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Notifications List
            if (uiState.isLoading) {
                LoadingState(message = "Memuat notifikasi...")
            } else if (uiState.filteredNotifications.isEmpty()) {
                EmptyState(
                    title = "Tidak Ada Notifikasi",
                    message = "Saat ini tidak ada notifikasi baru di kategori ini.",
                    icon = Icons.Default.NotificationsNone
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(uiState.filteredNotifications, key = { it.id }) { item ->
                        NotificationItemCard(
                            item = item,
                            onClick = {
                                viewModel.markAsRead(item.id)
                                if (!item.reportId.isNullOrBlank()) {
                                    onNavigateToReportDetail(item.reportId)
                                }
                            }
                        )
                    }
                    item {
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun NotificationItemCard(
    item: NotificationItem,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(InfoBahayaThemeTokens.shapes.card)
            .clickable { onClick() },
        shape = InfoBahayaThemeTokens.shapes.card,
        colors = CardDefaults.cardColors(
            containerColor = if (!item.isRead) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f) else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (!item.isRead) 2.dp else 0.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(item.category.color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = item.category.icon,
                    contentDescription = null,
                    tint = item.category.color,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = if (!item.isRead) FontWeight.Bold else FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    if (!item.isRead) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(PrimaryBlue)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = item.message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = item.category.displayName,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = item.category.color
                    )
                    Text(
                        text = item.timestamp,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
