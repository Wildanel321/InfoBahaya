package com.example.infobahaya.domain.repository

import com.example.infobahaya.domain.model.EmergencyContact
import kotlinx.coroutines.flow.Flow

interface EmergencyRepository {
    fun getEmergencyContacts(): Flow<List<EmergencyContact>>
    suspend fun triggerEmergencyAlert(
        latitude: Double,
        longitude: Double,
        emergencyType: String,
        description: String
    ): Result<String>
}
