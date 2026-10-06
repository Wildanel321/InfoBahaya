package com.example.infobahaya.presentation.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAlert
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.PhoneInTalk
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.infobahaya.core.di.ServiceLocator
import com.example.infobahaya.core.theme.InfoBahayaThemeTokens
import com.example.infobahaya.core.theme.PrimaryBlue
import com.example.infobahaya.core.theme.SafetyNavy900
import com.example.infobahaya.core.theme.SeverityEmergency
import com.example.infobahaya.presentation.components.InfoBahayaButton
import com.example.infobahaya.presentation.components.InfoBahayaOutlinedButton
import kotlinx.coroutines.launch

data class OnboardingPageData(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val accentColor: Color,
    val badgeText: String
)

@Composable
fun OnboardingScreen(
    onFinishOnboarding: () -> Unit
) {
    val authRepository = ServiceLocator.provideAuthRepository()
    val scope = rememberCoroutineScope()

    val pages = listOf(
        OnboardingPageData(
            title = "Pantau Bahaya Sekitar Secara Real-Time",
            subtitle = "Peta interaktif cerdas menyajikan titik bahaya jalan rusak, banjir, pohon tumbang, dan ancaman publik di rute perjalanan Anda.",
            icon = Icons.Default.Map,
            accentColor = PrimaryBlue,
            badgeText = "PETA INTERAKTIF"
        ),
        OnboardingPageData(
            title = "Laporkan Masalah Cepat & Tepat Sasaran",
            subtitle = "Sertakan foto bukti dan lokasi GPS akurat. Laporan Anda langsung diteruskan dan diverifikasi ke dinas terkait (PU, PLN, Damkar, Polisi).",
            icon = Icons.Default.AddAlert,
            accentColor = Color(0xFF10B981),
            badgeText = "PELAPORAN TERVERIFIKASI"
        ),
        OnboardingPageData(
            title = "Panggilan Cepat Darurat 24 Jam",
            subtitle = "Akses instan bebas pulsa ke nomor darurat 112, 110, 113, 119, serta broadcast peringatan dini jika berada di area berisiko tinggi.",
            icon = Icons.Default.PhoneInTalk,
            accentColor = SeverityEmergency,
            badgeText = "TANGGAP DARURAT"
        )
    )

    val pagerState = rememberPagerState(pageCount = { pages.size })

    fun completeAndNavigate() {
        authRepository.setOnboardingCompleted(true)
        onFinishOnboarding()
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = InfoBahayaThemeTokens.spacing.screenPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Bar with Skip Button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (pagerState.currentPage < pages.size - 1) {
                    TextButton(onClick = { completeAndNavigate() }) {
                        Text(
                            text = "Lewati",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    Spacer(modifier = Modifier.height(48.dp))
                }
            }

            // Pager Content
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) { pageIdx ->
                val page = pages[pageIdx]
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Visual Graphic Card
                    Box(
                        modifier = Modifier
                            .size(180.dp)
                            .clip(RoundedCornerShape(36.dp))
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(
                                        page.accentColor.copy(alpha = 0.25f),
                                        page.accentColor.copy(alpha = 0.05f)
                                    )
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(120.dp)
                                .clip(RoundedCornerShape(28.dp))
                                .background(page.accentColor),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = page.icon,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(64.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // Badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(page.accentColor.copy(alpha = 0.12f))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = page.badgeText,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = page.accentColor
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = page.title,
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onBackground,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = page.subtitle,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        lineHeight = MaterialTheme.typography.bodyMedium.lineHeight
                    )
                }
            }

            // Pager Indicators
            Row(
                modifier = Modifier
                    .padding(vertical = 24.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(pages.size) { iteration ->
                    val isSelected = pagerState.currentPage == iteration
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .height(8.dp)
                            .width(if (isSelected) 28.dp else 8.dp)
                            .clip(CircleShape)
                            .background(
                                if (isSelected) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.outlineVariant
                            )
                    )
                }
            }

            // Bottom CTA Buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (pagerState.currentPage > 0) {
                    InfoBahayaOutlinedButton(
                        text = "Kembali",
                        onClick = {
                            scope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage - 1)
                            }
                        },
                        modifier = Modifier.weight(1f)
                    )
                }

                InfoBahayaButton(
                    text = if (pagerState.currentPage == pages.size - 1) "Mulai Sekarang" else "Lanjut",
                    onClick = {
                        if (pagerState.currentPage < pages.size - 1) {
                            scope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage + 1)
                            }
                        } else {
                            completeAndNavigate()
                        }
                    },
                    modifier = Modifier.weight(if (pagerState.currentPage > 0) 1.5f else 1f)
                )
            }
        }
    }
}
