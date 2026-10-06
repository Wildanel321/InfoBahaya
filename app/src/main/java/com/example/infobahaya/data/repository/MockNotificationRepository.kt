package com.example.infobahaya.data.repository

import com.example.infobahaya.domain.model.NotificationCategory
import com.example.infobahaya.domain.model.NotificationItem
import com.example.infobahaya.domain.repository.NotificationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map

class MockNotificationRepository : NotificationRepository {

    private val initialNotifications = listOf(
        NotificationItem(
            id = "notif_01",
            title = "Status Laporan Diperbarui",
            message = "Laporan kabel listrik putus di Jl. Menteng Raya telah 'Diproses' oleh Tim Yantek PLN.",
            category = NotificationCategory.REPORT_UPDATE,
            timestamp = "15 menit lalu",
            isRead = false,
            reportId = "REP-2026-001"
        ),
        NotificationItem(
            id = "notif_02",
            title = "Peringatan Bahaya Sekitar Anda (1.2 km)",
            message = "Pohon tumbang menutup jalan di Jl. M.H. Thamrin. Silakan gunakan jalur alternatif Rasuna Said.",
            category = NotificationCategory.NEARBY_HAZARD,
            timestamp = "1 jam lalu",
            isRead = false,
            reportId = "REP-2026-002"
        ),
        NotificationItem(
            id = "notif_03",
            title = "Laporan Anda Telah Diverifikasi",
            message = "Laporan titik kriminalitas di TB Simatupang telah diverifikasi dan diteruskan ke Polsek Pasar Minggu.",
            category = NotificationCategory.VERIFICATION,
            timestamp = "5 jam lalu",
            isRead = true,
            reportId = "REP-2026-006"
        ),
        NotificationItem(
            id = "notif_04",
            title = "Peringatan Dini Cuaca Ekstrem BMKG",
            message = "Potensi hujan lebat disertai angin kencang di wilayah DKI Jakarta sore hari ini. Waspada banjir dan pohon tumbang.",
            category = NotificationCategory.EMERGENCY,
            timestamp = "Kemarin, 16:00",
            isRead = true
        ),
        NotificationItem(
            id = "notif_05",
            title = "Info Sistem InfoBahaya v2.0",
            message = "Fitur offline mode dan sync otomatis kini telah aktif. Laporan Anda tetap aman meski tanpa koneksi internet.",
            category = NotificationCategory.SYSTEM,
            timestamp = "2 hari lalu",
            isRead = true
        )
    )

    private val _notifications = MutableStateFlow<List<NotificationItem>>(initialNotifications)

    override fun getNotifications(category: NotificationCategory?): Flow<List<NotificationItem>> {
        return _notifications.map { list ->
            if (category == null) list else list.filter { it.category == category }
        }
    }

    override fun getUnreadCount(): Flow<Int> {
        return _notifications.map { list -> list.count { !it.isRead } }
    }

    override suspend fun markAsRead(id: String): Result<Unit> {
        val list = _notifications.value.map { item ->
            if (item.id == id) item.copy(isRead = true) else item
        }
        _notifications.value = list
        return Result.success(Unit)
    }

    override suspend fun markAllAsRead(): Result<Unit> {
        val list = _notifications.value.map { it.copy(isRead = true) }
        _notifications.value = list
        return Result.success(Unit)
    }

    override suspend fun addNotification(item: NotificationItem): Result<Unit> {
        val list = _notifications.value.toMutableList()
        list.add(0, item)
        _notifications.value = list
        return Result.success(Unit)
    }
}
