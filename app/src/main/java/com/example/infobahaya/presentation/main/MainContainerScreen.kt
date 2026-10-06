package com.example.infobahaya.presentation.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ListAlt
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhoneInTalk
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.infobahaya.core.di.ServiceLocator
import com.example.infobahaya.core.theme.PrimaryBlue
import com.example.infobahaya.core.theme.SeverityEmergency
import com.example.infobahaya.navigation.BottomNavItem
import com.example.infobahaya.presentation.emergency.EmergencyScreen
import com.example.infobahaya.presentation.home.HomeScreen
import com.example.infobahaya.presentation.notification.NotificationScreen
import com.example.infobahaya.presentation.profile.ProfileScreen
import com.example.infobahaya.presentation.tracking.ReportHistoryScreen

data class NavTabItem(
    val item: BottomNavItem,
    val title: String,
    val icon: ImageVector,
    val isEmergency: Boolean = false
)

@Composable
fun MainContainerScreen(
    onNavigateToReportDetail: (String) -> Unit,
    onNavigateToCreateReport: () -> Unit,
    onNavigateToEditProfile: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToModeratorDashboard: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(BottomNavItem.HOME) }
    val notificationRepository = ServiceLocator.provideNotificationRepository()
    val unreadNotifCount by notificationRepository.getUnreadCount().collectAsState(initial = 2)

    val tabs = listOf(
        NavTabItem(BottomNavItem.HOME, "Peta", Icons.Default.Map),
        NavTabItem(BottomNavItem.TRACKING, "Riwayat", Icons.AutoMirrored.Filled.ListAlt),
        NavTabItem(BottomNavItem.EMERGENCY, "Darurat", Icons.Default.PhoneInTalk, isEmergency = true),
        NavTabItem(BottomNavItem.NOTIFICATIONS, "Notifikasi", Icons.Default.Notifications),
        NavTabItem(BottomNavItem.PROFILE, "Profil", Icons.Default.Person)
    )

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = PrimaryBlue,
                tonalElevation = 8.dp
            ) {
                tabs.forEach { tab ->
                    val isSelected = selectedTab == tab.item
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { selectedTab = tab.item },
                        icon = {
                            if (tab.item == BottomNavItem.NOTIFICATIONS && unreadNotifCount > 0) {
                                BadgedBox(
                                    badge = {
                                        Badge(
                                            containerColor = SeverityEmergency,
                                            contentColor = Color.White
                                        ) {
                                            Text("$unreadNotifCount")
                                        }
                                    }
                                ) {
                                    Icon(
                                        imageVector = tab.icon,
                                        contentDescription = tab.title,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                            } else {
                                Icon(
                                    imageVector = tab.icon,
                                    contentDescription = tab.title,
                                    tint = if (tab.isEmergency && isSelected) SeverityEmergency else Color.Unspecified,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        },
                        label = {
                            Text(
                                text = tab.title,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = if (tab.isEmergency) SeverityEmergency else PrimaryBlue,
                            selectedTextColor = if (tab.isEmergency) SeverityEmergency else PrimaryBlue,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            indicatorColor = if (tab.isEmergency) SeverityEmergency.copy(alpha = 0.15f) else PrimaryBlue.copy(alpha = 0.12f)
                        )
                    )
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (selectedTab) {
                BottomNavItem.HOME -> HomeScreen(
                    onNavigateToReportDetail = onNavigateToReportDetail,
                    onNavigateToCreateReport = onNavigateToCreateReport,
                    onNavigateToEmergency = { selectedTab = BottomNavItem.EMERGENCY }
                )
                BottomNavItem.TRACKING -> ReportHistoryScreen(
                    onNavigateToDetail = onNavigateToReportDetail,
                    onNavigateToCreateReport = onNavigateToCreateReport
                )
                BottomNavItem.EMERGENCY -> EmergencyScreen(
                    onNavigateToReportDetail = onNavigateToReportDetail
                )
                BottomNavItem.NOTIFICATIONS -> NotificationScreen(
                    onNavigateToReportDetail = onNavigateToReportDetail
                )
                BottomNavItem.PROFILE -> ProfileScreen(
                    onNavigateToEditProfile = onNavigateToEditProfile,
                    onNavigateToSettings = onNavigateToSettings,
                    onNavigateToModeratorDashboard = onNavigateToModeratorDashboard,
                    onNavigateToLogin = onNavigateToLogin
                )
            }
        }
    }
}
