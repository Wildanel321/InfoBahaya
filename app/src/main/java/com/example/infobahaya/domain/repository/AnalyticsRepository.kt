package com.example.infobahaya.domain.repository

import com.example.infobahaya.domain.model.AnalyticsData
import kotlinx.coroutines.flow.Flow

interface AnalyticsRepository {
    fun getAnalyticsData(): Flow<AnalyticsData>
}
