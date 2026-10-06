package com.example.infobahaya.core.di

import com.example.infobahaya.data.repository.MockAnalyticsRepository
import com.example.infobahaya.data.repository.MockAuthRepository
import com.example.infobahaya.data.repository.MockEmergencyRepository
import com.example.infobahaya.data.repository.MockHazardReportRepository
import com.example.infobahaya.data.repository.MockNotificationRepository
import com.example.infobahaya.domain.repository.AnalyticsRepository
import com.example.infobahaya.domain.repository.AuthRepository
import com.example.infobahaya.domain.repository.EmergencyRepository
import com.example.infobahaya.domain.repository.HazardReportRepository
import com.example.infobahaya.domain.repository.NotificationRepository

object ServiceLocator {

    private val authRepositoryInstance: AuthRepository by lazy {
        MockAuthRepository()
    }

    private val hazardReportRepositoryInstance: HazardReportRepository by lazy {
        MockHazardReportRepository()
    }

    private val notificationRepositoryInstance: NotificationRepository by lazy {
        MockNotificationRepository()
    }

    private val emergencyRepositoryInstance: EmergencyRepository by lazy {
        MockEmergencyRepository()
    }

    private val analyticsRepositoryInstance: AnalyticsRepository by lazy {
        MockAnalyticsRepository()
    }

    fun provideAuthRepository(): AuthRepository = authRepositoryInstance

    fun provideHazardReportRepository(): HazardReportRepository = hazardReportRepositoryInstance

    fun provideNotificationRepository(): NotificationRepository = notificationRepositoryInstance

    fun provideEmergencyRepository(): EmergencyRepository = emergencyRepositoryInstance

    fun provideAnalyticsRepository(): AnalyticsRepository = analyticsRepositoryInstance
}
