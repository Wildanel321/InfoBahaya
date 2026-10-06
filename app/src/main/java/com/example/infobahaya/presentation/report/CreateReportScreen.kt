package com.example.infobahaya.presentation.report

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Title
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.infobahaya.core.theme.InfoBahayaThemeTokens
import com.example.infobahaya.core.theme.PrimaryBlue
import com.example.infobahaya.core.theme.SeverityEmergency
import com.example.infobahaya.domain.model.HazardCategory
import com.example.infobahaya.domain.model.HazardSeverity
import com.example.infobahaya.presentation.components.InfoBahayaButton
import com.example.infobahaya.presentation.components.InfoBahayaOutlinedButton
import com.example.infobahaya.presentation.components.InfoBahayaTextField
import com.example.infobahaya.presentation.components.SeverityBadge

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateReportScreen(
    onNavigateBack: () -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToTracking: () -> Unit,
    viewModel: CreateReportViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    val stepTitles = listOf(
        "Kategori Bahaya",
        "Foto & Bukti Lapangan",
        "Titik Lokasi Kejadian",
        "Tingkat Bahaya (Severity)",
        "Judul & Deskripsi",
        "Tinjau & Konfirmasi",
        "Laporan Berhasil"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = if (uiState.currentStep <= 6) "STEP ${uiState.currentStep} OF 6" else "SELESAI",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = PrimaryBlue
                        )
                        Text(
                            text = stepTitles.getOrElse(uiState.currentStep - 1) { "Buat Laporan" },
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                },
                navigationIcon = {
                    if (uiState.currentStep in 1..6) {
                        IconButton(onClick = {
                            if (uiState.currentStep > 1) viewModel.prevStep() else onNavigateBack()
                        }) {
                            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        bottomBar = {
            if (uiState.currentStep in 1..6) {
                SurfaceBottomActions(
                    currentStep = uiState.currentStep,
                    isLoading = uiState.isLoading,
                    onPrev = {
                        if (uiState.currentStep > 1) viewModel.prevStep() else onNavigateBack()
                    },
                    onNext = { viewModel.nextStep() }
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Progress Line Indicator
            if (uiState.currentStep in 1..6) {
                LinearProgressIndicator(
                    progress = { (uiState.currentStep / 6f) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp),
                    color = PrimaryBlue,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )
            }

            // Error message banner
            AnimatedVisibility(visible = uiState.stepError != null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                    shape = InfoBahayaThemeTokens.shapes.small
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.ErrorOutline,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = uiState.stepError.orEmpty(),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
            }

            // Step Content Animated Transition
            AnimatedContent(
                targetState = uiState.currentStep,
                transitionSpec = {
                    if (targetState > initialState) {
                        slideInHorizontally { width -> width } + fadeIn() togetherWith
                                slideOutHorizontally { width -> -width } + fadeOut()
                    } else {
                        slideInHorizontally { width -> -width } + fadeIn() togetherWith
                                slideOutHorizontally { width -> width } + fadeOut()
                    }
                },
                label = "step_content",
                modifier = Modifier.weight(1f)
            ) { targetStep ->
                when (targetStep) {
                    1 -> Step1Category(
                        selectedCategory = uiState.category,
                        onSelectCategory = { viewModel.onSelectCategory(it) }
                    )
                    2 -> Step2Media(
                        photoUrls = uiState.photoUrls,
                        onAddPhoto = { viewModel.addSamplePhoto(it) },
                        onRemovePhoto = { viewModel.removePhoto(it) }
                    )
                    3 -> Step3Location(
                        address = uiState.address,
                        landmark = uiState.landmark,
                        latitude = uiState.latitude,
                        longitude = uiState.longitude,
                        onLandmarkChange = { viewModel.onLandmarkChange(it) },
                        onRefreshGps = { viewModel.refreshGpsLocation() }
                    )
                    4 -> Step4Severity(
                        selectedSeverity = uiState.severity,
                        onSelectSeverity = { viewModel.onSelectSeverity(it) }
                    )
                    5 -> Step5Description(
                        title = uiState.title,
                        description = uiState.description,
                        onTitleChange = { viewModel.onTitleChange(it) },
                        onDescriptionChange = { viewModel.onDescriptionChange(it) }
                    )
                    6 -> Step6Review(
                        uiState = uiState
                    )
                    7 -> Step7Success(
                        createdReport = uiState.createdReport,
                        isOffline = uiState.isOfflineMode,
                        onGoHome = onNavigateToHome,
                        onGoTracking = onNavigateToTracking
                    )
                }
            }
        }
    }
}

// -------------------------------------------------------------
// STEP 1: CATEGORY SELECTION
// -------------------------------------------------------------
@Composable
private fun Step1Category(
    selectedCategory: HazardCategory?,
    onSelectCategory: (HazardCategory) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Pilih Jenis Bahaya / Kendala",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = "Kategori yang tepat mempercepat penugasan ke dinas/instansi terkait.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(HazardCategory.entries) { category ->
                val isSelected = selectedCategory == category
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(InfoBahayaThemeTokens.shapes.card)
                        .border(
                            width = if (isSelected) 2.dp else 1.dp,
                            color = if (isSelected) category.tintColor else MaterialTheme.colorScheme.outline,
                            shape = InfoBahayaThemeTokens.shapes.card
                        )
                        .clickable { onSelectCategory(category) },
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) category.tintColor.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surface
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(category.tintColor.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = category.icon,
                                contentDescription = null,
                                tint = category.tintColor,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = category.displayName,
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = category.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// STEP 2: MEDIA PICKER
// -------------------------------------------------------------
@Composable
private fun Step2Media(
    photoUrls: List<String>,
    onAddPhoto: (String) -> Unit,
    onRemovePhoto: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = "Unggah Foto Bukti Kejadian",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = "Ambil foto langsung dengan kamera atau pilih dari galeri untuk mempermudah verifikasi.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Capture Buttons Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(110.dp)
                    .clip(InfoBahayaThemeTokens.shapes.card)
                    .background(PrimaryBlue.copy(alpha = 0.08f))
                    .border(1.5.dp, PrimaryBlue, InfoBahayaThemeTokens.shapes.card)
                    .clickable {
                        onAddPhoto("https://images.unsplash.com/photo-1544717305-2782549b5136?w=600")
                    },
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.PhotoCamera,
                        contentDescription = "Buka Kamera",
                        tint = PrimaryBlue,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Ambil Foto (Kamera)",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = PrimaryBlue
                    )
                }
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(110.dp)
                    .clip(InfoBahayaThemeTokens.shapes.card)
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    .border(1.dp, MaterialTheme.colorScheme.outline, InfoBahayaThemeTokens.shapes.card)
                    .clickable {
                        onAddPhoto("https://images.unsplash.com/photo-1513836279014-a89f7a76ae86?w=600")
                    },
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.AddPhotoAlternate,
                        contentDescription = "Galeri",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Pilih dari Galeri",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Foto Terlampir (${photoUrls.size}/4)",
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(10.dp))

        if (photoUrls.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .clip(InfoBahayaThemeTokens.shapes.card)
                    .background(MaterialTheme.colorScheme.surface)
                    .border(1.dp, MaterialTheme.colorScheme.outline, InfoBahayaThemeTokens.shapes.card),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Belum ada foto yang dipilih.\nKlik tombol kamera di atas untuk melampirkan foto.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            photoUrls.forEachIndexed { index, url ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    shape = InfoBahayaThemeTokens.shapes.small,
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF334155)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.PhotoCamera,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Bukti_Foto_${index + 1}.jpg",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Ukuran terkompresi • 1080p WebP (240 KB)",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        IconButton(onClick = { onRemovePhoto(index) }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Hapus Foto",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// STEP 3: LOCATION PICKER
// -------------------------------------------------------------
@Composable
private fun Step3Location(
    address: String,
    landmark: String,
    latitude: Double,
    longitude: Double,
    onLandmarkChange: (String) -> Unit,
    onRefreshGps: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = "Tentukan Titik Lokasi Kejadian",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = "GPS otomatis mengunci posisi Anda saat ini. Anda dapat menyempurnakan patokan agar petugas mudah menemukan lokasi.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(16.dp))

        // GPS Coordinates Box
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = InfoBahayaThemeTokens.shapes.card,
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = PrimaryBlue,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Koordinat Terkunci",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(PrimaryBlue.copy(alpha = 0.1f))
                            .clickable { onRefreshGps() }
                            .padding(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.MyLocation,
                            contentDescription = "Refresh GPS",
                            tint = PrimaryBlue,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "$latitude, $longitude (Akurasi ±4m)",
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                    color = PrimaryBlue
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = address,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        InfoBahayaTextField(
            value = landmark,
            onValueChange = onLandmarkChange,
            label = "Patokan Terdekat (Landmark)",
            placeholder = "Contoh: Seberang Alfamart, Depan halte busway, Ruko nomor 14",
            helperText = "Membantu regu penolong dan teknisi lapangan menuju titik pasti."
        )
    }
}

// -------------------------------------------------------------
// STEP 4: SEVERITY PICKER
// -------------------------------------------------------------
@Composable
private fun Step4Severity(
    selectedSeverity: HazardSeverity,
    onSelectSeverity: (HazardSeverity) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = "Tingkat Urgensi & Dampak Bahaya",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = "Pilih level keparahan sesuai kondisi di lapangan untuk prioritas penanganan.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(16.dp))

        HazardSeverity.entries.forEach { severity ->
            val isSelected = selectedSeverity == severity
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp)
                    .clip(InfoBahayaThemeTokens.shapes.card)
                    .border(
                        width = if (isSelected) 2.dp else 1.dp,
                        color = if (isSelected) severity.color else MaterialTheme.colorScheme.outline,
                        shape = InfoBahayaThemeTokens.shapes.card
                    )
                    .clickable { onSelectSeverity(severity) },
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) severity.containerColor else MaterialTheme.colorScheme.surface
                )
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(severity.color),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = severity.icon,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = severity.displayName.uppercase(),
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = if (isSelected) severity.contentColor else MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "• ${severity.slaText}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = severity.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    if (isSelected) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Terpilih",
                            tint = severity.color,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// STEP 5: TITLE & DESCRIPTION INPUT
// -------------------------------------------------------------
@Composable
private fun Step5Description(
    title: String,
    description: String,
    onTitleChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = "Rincian Deskripsi Kejadian",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = "Tuliskan informasi ringkas dan jelas mengenai bahaya yang terjadi.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(16.dp))

        InfoBahayaTextField(
            value = title,
            onValueChange = onTitleChange,
            label = "Judul Laporan Singkat",
            placeholder = "Contoh: Pohon tumbang menimpa kabel listrik di depan ruko",
            leadingIcon = Icons.Default.Title,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
        )

        Spacer(modifier = Modifier.height(16.dp))

        InfoBahayaTextField(
            value = description,
            onValueChange = onDescriptionChange,
            label = "Deskripsi Lengkap Kejadian",
            placeholder = "Jelaskan situasi terperinci: perkiraan waktu kejadian, apakah ada korban, dampaknya terhadap arus lalu lintas atau bahaya lanjutan...",
            singleLine = false,
            minLines = 4,
            maxLines = 8,
            helperText = "Minimal 15 karakter."
        )
    }
}

// -------------------------------------------------------------
// STEP 6: REVIEW SUMMARY
// -------------------------------------------------------------
@Composable
private fun Step6Review(
    uiState: CreateReportUiState
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = "Tinjau Ringkasan Laporan",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = "Pastikan semua data di bawah ini sudah akurat sebelum dikirimkan ke sistem.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = InfoBahayaThemeTokens.shapes.card,
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Category and Severity Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = uiState.category?.displayName ?: "Kategori Umum",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = uiState.category?.tintColor ?: PrimaryBlue
                    )
                    SeverityBadge(severity = uiState.severity)
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = uiState.title.ifBlank { "Laporan Bahaya Publik" },
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = uiState.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Location Box
                Row(verticalAlignment = Alignment.Top) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = PrimaryBlue,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Column {
                        Text(
                            text = uiState.address,
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        if (uiState.landmark.isNotBlank()) {
                            Text(
                                text = "Patokan: ${uiState.landmark}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "${uiState.photoUrls.size} Foto Bukti Terlampir",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                    color = PrimaryBlue
                )
            }
        }
    }
}

// -------------------------------------------------------------
// STEP 7: SUBMIT SUCCESS
// -------------------------------------------------------------
@Composable
private fun Step7Success(
    createdReport: com.example.infobahaya.domain.model.HazardReport?,
    isOffline: Boolean,
    onGoHome: () -> Unit,
    onGoTracking: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(90.dp)
                .clip(CircleShape)
                .background(Color(0xFFD1FAE5)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = Color(0xFF10B981),
                modifier = Modifier.size(54.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = if (isOffline) "Laporan Disimpan di Perangkat" else "Laporan Berhasil Terkirim!",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = if (isOffline)
                "Mode offline aktif. Laporan telah diamankan dan akan otomatis disinkronkan ke server saat koneksi internet kembali normal."
            else
                "Nomor Registrasi: ${createdReport?.id ?: "REP-2026"}. Laporan Anda langsung diteruskan ke tim moderator untuk proses verifikasi dan penanganan dinas terkait.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        InfoBahayaButton(
            text = "Pantau Status di Riwayat Laporan",
            onClick = onGoTracking
        )

        Spacer(modifier = Modifier.height(12.dp))

        InfoBahayaOutlinedButton(
            text = "Kembali ke Peta Utama",
            onClick = onGoHome
        )
    }
}

@Composable
private fun SurfaceBottomActions(
    currentStep: Int,
    isLoading: Boolean,
    onPrev: () -> Unit,
    onNext: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (currentStep > 1) {
                InfoBahayaOutlinedButton(
                    text = "Kembali",
                    onClick = onPrev,
                    modifier = Modifier.weight(1f)
                )
            }

            InfoBahayaButton(
                text = if (currentStep == 6) "Kirim Laporan Sekarang" else "Lanjut",
                onClick = onNext,
                isLoading = isLoading,
                trailingIcon = if (currentStep == 6) Icons.AutoMirrored.Filled.Send else null,
                modifier = Modifier.weight(if (currentStep > 1) 1.5f else 1f)
            )
        }
    }
}
