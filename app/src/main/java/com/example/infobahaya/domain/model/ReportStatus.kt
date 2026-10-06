package com.example.infobahaya.domain.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.PendingActions
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Sync
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.infobahaya.core.theme.StatusForwarded
import com.example.infobahaya.core.theme.StatusForwardedContainer
import com.example.infobahaya.core.theme.StatusForwardedText
import com.example.infobahaya.core.theme.StatusInProgress
import com.example.infobahaya.core.theme.StatusInProgressContainer
import com.example.infobahaya.core.theme.StatusInProgressText
import com.example.infobahaya.core.theme.StatusPending
import com.example.infobahaya.core.theme.StatusPendingContainer
import com.example.infobahaya.core.theme.StatusPendingText
import com.example.infobahaya.core.theme.StatusRejected
import com.example.infobahaya.core.theme.StatusRejectedContainer
import com.example.infobahaya.core.theme.StatusRejectedText
import com.example.infobahaya.core.theme.StatusResolved
import com.example.infobahaya.core.theme.StatusResolvedContainer
import com.example.infobahaya.core.theme.StatusResolvedText
import com.example.infobahaya.core.theme.StatusSubmitted
import com.example.infobahaya.core.theme.StatusSubmittedContainer
import com.example.infobahaya.core.theme.StatusSubmittedText
import com.example.infobahaya.core.theme.StatusVerified
import com.example.infobahaya.core.theme.StatusVerifiedContainer
import com.example.infobahaya.core.theme.StatusVerifiedText

enum class ReportStatus(
    val stepOrder: Int,
    val displayName: String,
    val description: String,
    val icon: ImageVector,
    val color: Color,
    val containerColor: Color,
    val contentColor: Color
) {
    DIKIRIM(
        stepOrder = 1,
        displayName = "Dikirim",
        description = "Laporan berhasil terkirim dan tersimpan di sistem",
        icon = Icons.Default.Send,
        color = StatusSubmitted,
        containerColor = StatusSubmittedContainer,
        contentColor = StatusSubmittedText
    ),
    MENUNGGU_VERIFIKASI(
        stepOrder = 2,
        displayName = "Menunggu Verifikasi",
        description = "Petugas moderator sedang meninjau validitas & bukti foto laporan",
        icon = Icons.Default.HourglassTop,
        color = StatusPending,
        containerColor = StatusPendingContainer,
        contentColor = StatusPendingText
    ),
    DIVERIFIKASI(
        stepOrder = 3,
        displayName = "Diverifikasi",
        description = "Laporan telah dinyatakan valid dan masuk prioritas penanganan",
        icon = Icons.Default.CheckCircle,
        color = StatusVerified,
        containerColor = StatusVerifiedContainer,
        contentColor = StatusVerifiedText
    ),
    DIPROSES(
        stepOrder = 4,
        displayName = "Diproses",
        description = "Tim teknis / lapangan sedang melakukan penanganan di lokasi",
        icon = Icons.Default.PendingActions,
        color = StatusInProgress,
        containerColor = StatusInProgressContainer,
        contentColor = StatusInProgressText
    ),
    DITERUSKAN(
        stepOrder = 5,
        displayName = "Diteruskan",
        description = "Diteruskan ke instansi terkait (Dinas PU / PLN / Damkar / Polisi / BPBD)",
        icon = Icons.Default.Sync,
        color = StatusForwarded,
        containerColor = StatusForwardedContainer,
        contentColor = StatusForwardedText
    ),
    SELESAI(
        stepOrder = 6,
        displayName = "Selesai",
        description = "Bahaya telah selesai ditangani dan kondisi lokasi kembali aman",
        icon = Icons.Default.AssignmentTurnedIn,
        color = StatusResolved,
        containerColor = StatusResolvedContainer,
        contentColor = StatusResolvedText
    ),
    DITOLAK(
        stepOrder = -1,
        displayName = "Ditolak",
        description = "Laporan tidak valid, duplikat, atau informasi kurang jelas",
        icon = Icons.Default.Cancel,
        color = StatusRejected,
        containerColor = StatusRejectedContainer,
        contentColor = StatusRejectedText
    );

    companion object {
        fun fromName(name: String): ReportStatus {
            return entries.firstOrNull { it.name.equals(name, ignoreCase = true) || it.displayName.equals(name, ignoreCase = true) } ?: MENUNGGU_VERIFIKASI
        }
    }
}
