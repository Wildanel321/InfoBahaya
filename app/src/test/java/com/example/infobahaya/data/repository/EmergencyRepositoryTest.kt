package com.example.infobahaya.data.repository

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class EmergencyRepositoryTest {

    private lateinit var emergencyRepository: MockEmergencyRepository

    @Before
    fun setUp() {
        emergencyRepository = MockEmergencyRepository()
    }

    @Test
    fun testEmergencyContactsContainCriticalHotlines() = runTest {
        val contacts = emergencyRepository.getEmergencyContacts().first()
        assertTrue(contacts.isNotEmpty())
        assertTrue(contacts.any { it.number == "112" })
        assertTrue(contacts.any { it.number == "110" })
        assertTrue(contacts.any { it.number == "113" })
        assertTrue(contacts.any { it.number == "119" })
    }

    @Test
    fun testTriggerEmergencyAlert() = runTest {
        val result = emergencyRepository.triggerEmergencyAlert(
            latitude = -6.2088,
            longitude = 106.8456,
            emergencyType = "Kebakaran",
            description = "Ledakan gardu listrik dekat perumahan"
        )
        assertTrue(result.isSuccess)
    }
}
