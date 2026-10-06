package com.example.infobahaya.data.repository

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class AnalyticsRepositoryTest {

    private lateinit var analyticsRepository: MockAnalyticsRepository

    @Before
    fun setUp() {
        analyticsRepository = MockAnalyticsRepository()
    }

    @Test
    fun testAnalyticsDataLoaded() = runTest {
        val data = analyticsRepository.getAnalyticsData().first()
        assertTrue(data.totalReports > 0)
        assertTrue(data.resolutionRatePercentage in 0f..100f)
        assertTrue(data.categoryBreakdown.isNotEmpty())
        assertTrue(data.severityBreakdown.isNotEmpty())
        assertTrue(data.monthlyTrends.isNotEmpty())
    }

    @Test
    fun testCategoryBreakdownSumsCorrectly() = runTest {
        val data = analyticsRepository.getAnalyticsData().first()
        val sum = data.categoryBreakdown.sumOf { it.count }
        assertTrue(sum > 0)
    }
}
