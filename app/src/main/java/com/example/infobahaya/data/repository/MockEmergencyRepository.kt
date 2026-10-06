package com.example.infobahaya.data.repository

import com.example.infobahaya.domain.model.EmergencyContact
import com.example.infobahaya.domain.model.EmergencyType
import com.example.infobahaya.domain.repository.EmergencyRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class MockEmergencyRepository : EmergencyRepository {

    private val contacts = listOf(
        EmergencyContact(
            id = "em_112",
            name = "Panggilan Darurat Terpadu (Semua Kejadian)",
            number = "112",
            type = EmergencyType.DARURAT_112,
            description = "Layanan cepat tanggap darurat resmi 24 jam bebas pulsa untuk medis, kebakaran, bencana alam, dan keamanan.",
            is24Hours = true,
            isTollFree = true
        ),
        EmergencyContact(
            id = "em_110",
            name = "Kepolisian Republik Indonesia (Polisi)",
            number = "110",
            type = EmergencyType.POLISI_110,
            description = "Laporan tindak kriminal, begal, kecelakaan lalu lintas parah, gangguan keamanan, dan ketertiban umum.",
            is24Hours = true,
            isTollFree = true
        ),
        EmergencyContact(
            id = "em_113",
            name = "Pemadam Kebakaran (Damkar)",
            number = "113",
            type = EmergencyType.DAMKAR_113,
            description = "Kebakaran gedung, pemukiman, evakuasi sarang tawon, pelepasan cincin darurat, dan evakuasi hewan berbisa.",
            is24Hours = true,
            isTollFree = true
        ),
        EmergencyContact(
            id = "em_119",
            name = "Ambulans & Gawat Darurat Medis (Kemenkes)",
            number = "119",
            type = EmergencyType.AMBULANS_119,
            description = "Penjemputan pasien darurat kritis, kecelakaan lalu lintas korban luka berat, serangan jantung, dan stroke.",
            is24Hours = true,
            isTollFree = true
        ),
        EmergencyContact(
            id = "em_sar",
            name = "BASARNAS (Pencarian & Pertolongan)",
            number = "115",
            type = EmergencyType.SAR_BASARNAS,
            description = "Operasi SAR bencana longsor, orang tenggelam/hanyut, banjir bandang, evakuasi reruntuhan bangunan.",
            is24Hours = true,
            isTollFree = true
        ),
        EmergencyContact(
            id = "em_pln",
            name = "PLN Contact Center (Bahaya Listrik)",
            number = "123",
            type = EmergencyType.PLN_GANGGUAN,
            description = "Laporan kabel putus bertegangan, tiang listrik patah, ledakan gardu trafo, korsleting skala besar.",
            is24Hours = true,
            isTollFree = false
        )
    )

    private val _contactsFlow = MutableStateFlow<List<EmergencyContact>>(contacts)

    override fun getEmergencyContacts(): Flow<List<EmergencyContact>> = _contactsFlow.asStateFlow()

    override suspend fun triggerEmergencyAlert(
        latitude: Double,
        longitude: Double,
        emergencyType: String,
        description: String
    ): Result<String> {
        delay(800)
        return Result.success("Sinyal SOS Darurat berhasil dipancarkan ke Posko Darurat 112 dan warga sekitar dalam radius 3 km.")
    }
}
