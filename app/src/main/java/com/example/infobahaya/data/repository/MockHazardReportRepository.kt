package com.example.infobahaya.data.repository

import com.example.infobahaya.domain.model.DraftReport
import com.example.infobahaya.domain.model.HazardCategory
import com.example.infobahaya.domain.model.HazardReport
import com.example.infobahaya.domain.model.HazardSeverity
import com.example.infobahaya.domain.model.ReportStatus
import com.example.infobahaya.domain.model.TimelineEvent
import com.example.infobahaya.domain.repository.HazardReportRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MockHazardReportRepository : HazardReportRepository {

    private val dateFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale("id", "ID"))

    private val initialReports = mutableListOf(
        HazardReport(
            id = "REP-2026-001",
            title = "Kabel Listrik Tegangan Tinggi Putus Menjuntai ke Jalan",
            description = "Kabel PLN putus akibat tertimpa dahan saat angin kencang tadi malam. Mengeluarkan percikan api kecil dan membahayakan pengendara motor serta pejalan kaki yang melintas di depan SDN 01.",
            category = HazardCategory.LISTRIK_KABEL,
            severity = HazardSeverity.DARURAT,
            status = ReportStatus.DIPROSES,
            latitude = -6.2088,
            longitude = 106.8456,
            address = "Jl. Menteng Raya No. 18, Menteng, Jakarta Pusat",
            landmark = "Depan SDN Menteng 01",
            photoUrls = listOf("https://images.unsplash.com/photo-1544717305-2782549b5136?w=600"),
            createdAt = "06 Okt 2026, 08:30",
            updatedAt = "06 Okt 2026, 09:15",
            userId = "user_101",
            userName = "Rian Pratama",
            userPhone = "081234567890",
            verifiedBy = "Budi Setiawan (Moderator #04)",
            moderatorNote = "Laporan valid dan terverifikasi darurat. Tim teknis PLN Rayon Menteng telah dikerahkan ke lokasi.",
            assignedDepartment = "PLN UID Jakarta Raya (Tim Yantek)",
            isPriority = true,
            isSynced = true,
            syncStatusText = "Tersinkronkan",
            upvotesCount = 28,
            isUpvotedByCurrentUser = true,
            timeline = listOf(
                TimelineEvent(
                    id = "tl_01",
                    reportId = "REP-2026-001",
                    status = ReportStatus.DIKIRIM,
                    title = "Laporan Terkirim",
                    description = "Laporan berhasil didaftarkan oleh pelapor ke sistem InfoBahaya.",
                    timestamp = "06 Okt 2026, 08:30",
                    actorName = "Rian Pratama",
                    actorRole = "Warga Pelapor"
                ),
                TimelineEvent(
                    id = "tl_02",
                    reportId = "REP-2026-001",
                    status = ReportStatus.DIVERIFIKASI,
                    title = "Laporan Diverifikasi & Masuk Prioritas Darurat",
                    description = "Moderator memvalidasi foto kabel putus dan menaikkan prioritas penanganan ke instansi berwenang.",
                    timestamp = "06 Okt 2026, 08:45",
                    actorName = "Budi Setiawan",
                    actorRole = "Moderator Piket"
                ),
                TimelineEvent(
                    id = "tl_03",
                    reportId = "REP-2026-001",
                    status = ReportStatus.DIPROSES,
                    title = "Petugas Yantek PLN Sedang Melakukan Isolasi Arus",
                    description = "Armada teknis darurat tiba di lokasi dan sedang mengamankan kabel serta memasang batas aman.",
                    timestamp = "06 Okt 2026, 09:15",
                    actorName = "Tim Yantek PLN",
                    actorRole = "Petugas Lapangan"
                )
            )
        ),
        HazardReport(
            id = "REP-2026-002",
            title = "Pohon Randu Besar Tumbang Menutup Total Badan Jalan",
            description = "Pohon peneduh tumbang melintang menutupi 2 lajur jalan utama. Arus lalu lintas tersendat total dari arah Thamrin menuju Bundaran HI.",
            category = HazardCategory.POHON_TUMBANG,
            severity = HazardSeverity.TINGGI,
            status = ReportStatus.DITERUSKAN,
            latitude = -6.1935,
            longitude = 106.8234,
            address = "Jl. M.H. Thamrin No. 8, Kebon Sirih, Jakarta Pusat",
            landmark = "Dekat Halte Transjakarta Bank Indonesia",
            photoUrls = listOf("https://images.unsplash.com/photo-1513836279014-a89f7a76ae86?w=600"),
            createdAt = "06 Okt 2026, 07:45",
            updatedAt = "06 Okt 2026, 08:20",
            userId = "user_202",
            userName = "Siti Nurhaliza",
            verifiedBy = "Agus Wicaksono (Mod #12)",
            moderatorNote = "Diteruskan ke Tim Reaksi Cepat Dinas Pertamanan & Dishub untuk rekayasa lalu lintas.",
            assignedDepartment = "Dinas Pertamanan dan Hutan Kota DKI",
            isPriority = true,
            isSynced = true,
            syncStatusText = "Tersinkronkan",
            upvotesCount = 45,
            isUpvotedByCurrentUser = false,
            timeline = listOf(
                TimelineEvent(
                    id = "tl_11",
                    reportId = "REP-2026-002",
                    status = ReportStatus.DIKIRIM,
                    title = "Laporan Diterima",
                    description = "Pelapor mengirimkan laporan pohon tumbang beserta bukti foto.",
                    timestamp = "06 Okt 2026, 07:45",
                    actorName = "Siti Nurhaliza",
                    actorRole = "Warga Pelapor"
                ),
                TimelineEvent(
                    id = "tl_12",
                    reportId = "REP-2026-002",
                    status = ReportStatus.DITERUSKAN,
                    title = "Diteruskan ke Dinas Pertamanan & Dishub",
                    description = "Permintaan evakuasi pohon dengan gergaji mesin dan pengalihan rute Transjakarta.",
                    timestamp = "06 Okt 2026, 08:20",
                    actorName = "Agus Wicaksono",
                    actorRole = "Moderator"
                )
            )
        ),
        HazardReport(
            id = "REP-2026-003",
            title = "Jalan Berlubang Dalam (Diameter 1.2m) di Jalur Kanan",
            description = "Lubang aspal amblas sedalam kurang lebih 15cm tidak terlihat jelas pada malam hari, sudah ada 2 pengendara motor yang hampir terjatuh.",
            category = HazardCategory.JALAN_RUSAK,
            severity = HazardSeverity.SEDANG,
            status = ReportStatus.MENUNGGU_VERIFIKASI,
            latitude = -6.2255,
            longitude = 106.8097,
            address = "Jl. Gatot Subroto Kav. 52, Semanggi, Jakarta Selatan",
            landmark = "50 meter sebelum flyover Kuningan",
            photoUrls = listOf("https://images.unsplash.com/photo-1515162816999-a0c47dc192f7?w=600"),
            createdAt = "06 Okt 2026, 10:10",
            updatedAt = "06 Okt 2026, 10:10",
            userId = "user_101",
            userName = "Rian Pratama",
            assignedDepartment = "Suku Dinas Bina Marga Jakarta Selatan",
            isPriority = false,
            isSynced = true,
            syncStatusText = "Tersinkronkan",
            upvotesCount = 12,
            isUpvotedByCurrentUser = false,
            timeline = listOf(
                TimelineEvent(
                    id = "tl_21",
                    reportId = "REP-2026-003",
                    status = ReportStatus.DIKIRIM,
                    title = "Laporan Didaftarkan",
                    description = "Menunggu verifikasi moderator wilayah Jakarta Selatan.",
                    timestamp = "06 Okt 2026, 10:10",
                    actorName = "Rian Pratama",
                    actorRole = "Warga Pelapor"
                )
            )
        ),
        HazardReport(
            id = "REP-2026-004",
            title = "Genangan Banjir 40-50 cm Merendam Pemukiman dan Jalan Warga",
            description = "Luapan saluran kali penghubung akibat hujan deras semalam. Akses kendaraan roda dua lumpuh dan air mulai masuk ke teras rumah warga RT 05/08.",
            category = HazardCategory.BANJIR,
            severity = HazardSeverity.TINGGI,
            status = ReportStatus.DIPROSES,
            latitude = -6.2589,
            longitude = 106.8621,
            address = "Jl. Raya Cipinang Indah, Kalimalang, Jakarta Timur",
            landmark = "Dekat jembatan kali penghubung",
            photoUrls = listOf("https://images.unsplash.com/photo-1547683905-f686c993aae5?w=600"),
            createdAt = "06 Okt 2026, 06:15",
            updatedAt = "06 Okt 2026, 07:00",
            userId = "user_303",
            userName = "Hendra Gunawan",
            assignedDepartment = "BPBD DKI Jakarta & Dinas SDA",
            isPriority = true,
            isSynced = true,
            syncStatusText = "Tersinkronkan",
            upvotesCount = 67,
            isUpvotedByCurrentUser = true,
            timeline = listOf(
                TimelineEvent(
                    id = "tl_31",
                    reportId = "REP-2026-004",
                    status = ReportStatus.DIKIRIM,
                    title = "Laporan Terkirim",
                    description = "Laporan genangan air di Cipinang Indah diterima.",
                    timestamp = "06 Okt 2026, 06:15",
                    actorName = "Hendra Gunawan",
                    actorRole = "Warga Pelapor"
                ),
                TimelineEvent(
                    id = "tl_32",
                    reportId = "REP-2026-004",
                    status = ReportStatus.DIPROSES,
                    title = "Pompa Mobile Dikerahkan",
                    description = "Dinas SDA mengerahkan 2 unit pompa mobile untuk menyedot genangan ke kali utama.",
                    timestamp = "06 Okt 2026, 07:00",
                    actorName = "Dinas SDA",
                    actorRole = "Petugas Lapangan"
                )
            )
        ),
        HazardReport(
            id = "REP-2026-005",
            title = "Kebakaran Sampah Liar Merembet ke Dekat Gardu Listrik",
            description = "Pembakaran sampah ilegal di lahan kosong tidak dijaga dan api mulai membesar mendekati gardu trafo PLN.",
            category = HazardCategory.KEBAKARAN,
            severity = HazardSeverity.DARURAT,
            status = ReportStatus.SELESAI,
            latitude = -6.1687,
            longitude = 106.7891,
            address = "Jl. Daan Mogot KM 12, Cengkareng, Jakarta Barat",
            landmark = "Belakang ruko otomotif",
            photoUrls = listOf("https://images.unsplash.com/photo-1527482797697-8795b05a13fe?w=600"),
            createdAt = "05 Okt 2026, 19:20",
            updatedAt = "05 Okt 2026, 20:45",
            userId = "user_404",
            userName = "Dewi Lestari",
            verifiedBy = "Budi Setiawan",
            moderatorNote = "2 unit mobil Damkar Pos Cengkareng langsung meluncur dan api berhasil dipadamkan.",
            assignedDepartment = "Dinas Penanggulangan Kebakaran dan Penyelamatan (Damkar)",
            isPriority = true,
            isSynced = true,
            syncStatusText = "Tersinkronkan",
            upvotesCount = 54,
            isUpvotedByCurrentUser = false,
            resolutionNote = "Api berhasil dipadamkan total dalam 40 menit, gardu listrik aman dari kobaran api.",
            timeline = listOf(
                TimelineEvent(
                    id = "tl_41",
                    reportId = "REP-2026-005",
                    status = ReportStatus.DIKIRIM,
                    title = "Laporan Darurat Diterima",
                    description = "Warga melaporkan kebakaran sampah dekat instalasi listrik.",
                    timestamp = "05 Okt 2026, 19:20",
                    actorName = "Dewi Lestari",
                    actorRole = "Warga Pelapor"
                ),
                TimelineEvent(
                    id = "tl_42",
                    reportId = "REP-2026-005",
                    status = ReportStatus.DIPROSES,
                    title = "2 Unit Armada Damkar Tiba di Lokasi",
                    description = "Petugas melakukan pemadaman dan pendinginan area dekat gardu PLN.",
                    timestamp = "05 Okt 2026, 19:35",
                    actorName = "Damkar Pos Cengkareng",
                    actorRole = "Tim Pemadam"
                ),
                TimelineEvent(
                    id = "tl_43",
                    reportId = "REP-2026-005",
                    status = ReportStatus.SELESAI,
                    title = "Penanganan Tuntas & Area Aman",
                    description = "Api padam total, tidak ada korban jiwa ataupun kerusakan fasilitas publik.",
                    timestamp = "05 Okt 2026, 20:45",
                    actorName = "Damkar DKI",
                    actorRole = "Komandan Pleton"
                )
            )
        ),
        HazardReport(
            id = "REP-2026-006",
            title = "Aktivitas Kriminalitas / Modus Begal Malam Hari di Jalur Sepi",
            description = "Sekelompok pemuda mencurigakan sering berkumpul dan menghadang pengendara roda dua di tikungan gelap tanpa penerangan jalan.",
            category = HazardCategory.KRIMINAL,
            severity = HazardSeverity.TINGGI,
            status = ReportStatus.DIVERIFIKASI,
            latitude = -6.2891,
            longitude = 106.8123,
            address = "Jl. TB Simatupang Gang Kancil, Pasar Minggu, Jakarta Selatan",
            landmark = "Dekat jembatan penyeberangan tol lama",
            photoUrls = listOf("https://images.unsplash.com/photo-1509198397868-475647b2a1e5?w=600"),
            createdAt = "06 Okt 2026, 01:10",
            updatedAt = "06 Okt 2026, 02:00",
            userId = "user_101",
            userName = "Rian Pratama",
            verifiedBy = "Budi Setiawan",
            moderatorNote = "Diteruskan ke Polsek Pasar Minggu untuk patroli intensif malam hari.",
            assignedDepartment = "Polsek Pasar Minggu & Satpol PP",
            isPriority = true,
            isSynced = true,
            syncStatusText = "Tersinkronkan",
            upvotesCount = 89,
            isUpvotedByCurrentUser = true,
            timeline = listOf(
                TimelineEvent(
                    id = "tl_51",
                    reportId = "REP-2026-006",
                    status = ReportStatus.DIKIRIM,
                    title = "Laporan Ancaman Keamanan Dikirim",
                    description = "Laporan titik rawan kriminalitas diterima.",
                    timestamp = "06 Okt 2026, 01:10",
                    actorName = "Rian Pratama",
                    actorRole = "Warga Pelapor"
                ),
                TimelineEvent(
                    id = "tl_52",
                    reportId = "REP-2026-006",
                    status = ReportStatus.DIVERIFIKASI,
                    title = "Koordinasi dengan Tim Patroli Presisi Polsek",
                    description = "Disposisi patroli malam dan usulan perbaikan lampu PJU ke Dishub.",
                    timestamp = "06 Okt 2026, 02:00",
                    actorName = "Budi Setiawan",
                    actorRole = "Moderator"
                )
            )
        )
    )

    private val _reports = MutableStateFlow<List<HazardReport>>(initialReports)
    private val _draftReport = MutableStateFlow<DraftReport?>(null)
    private val _isOffline = MutableStateFlow(false)
    private val _pendingSyncQueue = MutableStateFlow<List<HazardReport>>(emptyList())

    override fun getAllReports(): Flow<List<HazardReport>> = _reports.asStateFlow()

    override fun getReports(
        category: HazardCategory?,
        severity: HazardSeverity?,
        status: ReportStatus?,
        searchQuery: String
    ): Flow<List<HazardReport>> {
        return _reports.map { list ->
            list.filter { report ->
                val matchCategory = category == null || report.category == category
                val matchSeverity = severity == null || report.severity == severity
                val matchStatus = status == null || report.status == status
                val matchSearch = searchQuery.isBlank() ||
                        report.title.contains(searchQuery, ignoreCase = true) ||
                        report.description.contains(searchQuery, ignoreCase = true) ||
                        report.address.contains(searchQuery, ignoreCase = true) ||
                        report.category.displayName.contains(searchQuery, ignoreCase = true)
                matchCategory && matchSeverity && matchStatus && matchSearch
            }
        }
    }

    override fun getReportById(id: String): Flow<HazardReport?> {
        return _reports.map { list -> list.firstOrNull { it.id == id } }
    }

    override fun getUserReports(userId: String): Flow<List<HazardReport>> {
        return _reports.map { list -> list.filter { it.userId == userId } }
    }

    override fun getDraft(): Flow<DraftReport?> = _draftReport.asStateFlow()

    override suspend fun saveDraft(draft: DraftReport) {
        val now = dateFormat.format(Date())
        _draftReport.value = draft.copy(lastSavedAt = now)
    }

    override suspend fun clearDraft() {
        _draftReport.value = null
    }

    override suspend fun createReport(report: HazardReport): Result<HazardReport> {
        delay(600)
        val isCurrentlyOffline = _isOffline.value
        val now = dateFormat.format(Date())
        val generatedId = "REP-${SimpleDateFormat("yyyy", Locale.getDefault()).format(Date())}-${(100 + _reports.value.size + 1)}"

        val newReport = report.copy(
            id = if (report.id.isNotBlank() && report.id.startsWith("REP-")) report.id else generatedId,
            createdAt = now,
            updatedAt = now,
            isSynced = !isCurrentlyOffline,
            syncStatusText = if (isCurrentlyOffline) "Menunggu koneksi..." else "Tersinkronkan",
            timeline = listOf(
                TimelineEvent(
                    id = "tl_${System.currentTimeMillis()}",
                    reportId = generatedId,
                    status = ReportStatus.DIKIRIM,
                    title = "Laporan Berhasil Dibuat",
                    description = if (isCurrentlyOffline) "Disimpan lokal di perangkat, akan disinkronkan saat online." else "Laporan masuk antrian verifikasi moderator.",
                    timestamp = now,
                    actorName = report.userName,
                    actorRole = "Warga Pelapor"
                )
            )
        )

        val updatedList = _reports.value.toMutableList()
        updatedList.add(0, newReport)
        _reports.value = updatedList

        if (isCurrentlyOffline) {
            val pendingList = _pendingSyncQueue.value.toMutableList()
            pendingList.add(newReport)
            _pendingSyncQueue.value = pendingList
        }

        // Clear draft on successful creation
        clearDraft()
        return Result.success(newReport)
    }

    override suspend fun toggleUpvote(reportId: String): Result<HazardReport> {
        val list = _reports.value.toMutableList()
        val index = list.indexOfFirst { it.id == reportId }
        if (index == -1) return Result.failure(NoSuchElementException("Laporan tidak ditemukan"))

        val current = list[index]
        val isNowUpvoted = !current.isUpvotedByCurrentUser
        val newCount = if (isNowUpvoted) current.upvotesCount + 1 else maxOf(0, current.upvotesCount - 1)
        val updated = current.copy(
            upvotesCount = newCount,
            isUpvotedByCurrentUser = isNowUpvoted
        )
        list[index] = updated
        _reports.value = list
        return Result.success(updated)
    }

    override suspend fun updateReportStatus(
        reportId: String,
        newStatus: ReportStatus,
        moderatorNote: String,
        assignedDepartment: String?
    ): Result<HazardReport> {
        delay(400)
        val list = _reports.value.toMutableList()
        val index = list.indexOfFirst { it.id == reportId }
        if (index == -1) return Result.failure(NoSuchElementException("Laporan tidak ditemukan"))

        val current = list[index]
        val now = dateFormat.format(Date())

        val newTimelineEvent = TimelineEvent(
            id = "tl_${System.currentTimeMillis()}",
            reportId = reportId,
            status = newStatus,
            title = "Status Diperbarui: ${newStatus.displayName}",
            description = moderatorNote.ifBlank { newStatus.description },
            timestamp = now,
            actorName = "Budi Setiawan",
            actorRole = "Moderator",
            note = moderatorNote.ifBlank { null }
        )

        val updatedTimeline = current.timeline.toMutableList()
        updatedTimeline.add(newTimelineEvent)

        val updated = current.copy(
            status = newStatus,
            updatedAt = now,
            moderatorNote = moderatorNote.ifBlank { current.moderatorNote },
            assignedDepartment = assignedDepartment ?: current.assignedDepartment,
            timeline = updatedTimeline
        )

        list[index] = updated
        _reports.value = list
        return Result.success(updated)
    }

    override suspend fun syncPendingReports(): Result<Int> {
        delay(1200) // Simulated sync process
        val pending = _pendingSyncQueue.value
        val count = pending.size
        if (count == 0) return Result.success(0)

        val list = _reports.value.map { item ->
            if (pending.any { it.id == item.id }) {
                item.copy(isSynced = true, syncStatusText = "Tersinkronkan")
            } else {
                item
            }
        }
        _reports.value = list
        _pendingSyncQueue.value = emptyList()
        return Result.success(count)
    }

    override fun getOfflinePendingCount(): Flow<Int> {
        return _pendingSyncQueue.map { it.size }
    }

    override fun isOfflineMode(): Flow<Boolean> = _isOffline.asStateFlow()

    override suspend fun setOfflineMode(isOffline: Boolean) {
        _isOffline.value = isOffline
        if (!isOffline && _pendingSyncQueue.value.isNotEmpty()) {
            syncPendingReports()
        }
    }
}
