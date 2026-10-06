package com.example.infobahaya.data.repository

import com.example.infobahaya.domain.model.AnalyticsData
import com.example.infobahaya.domain.model.CategoryStat
import com.example.infobahaya.domain.model.HazardCategory
import com.example.infobahaya.domain.model.HazardSeverity
import com.example.infobahaya.domain.model.MonthlyTrend
import com.example.infobahaya.domain.model.SeverityStat
import com.example.infobahaya.domain.repository.AnalyticsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class MockAnalyticsRepository : AnalyticsRepository {

    private val analyticsData = AnalyticsData(
        totalReports = 142,
        newReportsCount = 18,
        inProgressCount = 34,
        resolvedCount = 82,
        priorityCount = 14,
        resolutionRatePercentage = 78.4f,
        averageResponseTimeHours = 3.2f,
        categoryBreakdown = listOf(
            CategoryStat(HazardCategory.JALAN_RUSAK, 48, 33.8f),
            CategoryStat(HazardCategory.POHON_TUMBANG, 26, 18.3f),
            CategoryStat(HazardCategory.BANJIR, 24, 16.9f),
            CategoryStat(HazardCategory.LISTRIK_KABEL, 18, 12.7f),
            CategoryStat(HazardCategory.KEBAKARAN, 12, 8.5f),
            CategoryStat(HazardCategory.KRIMINAL, 9, 6.3f),
            CategoryStat(HazardCategory.LONGSOR, 3, 2.1f),
            CategoryStat(HazardCategory.LAINNYA, 2, 1.4f)
        ),
        severityBreakdown = listOf(
            SeverityStat(HazardSeverity.DARURAT, 22, 15.5f),
            SeverityStat(HazardSeverity.TINGGI, 46, 32.4f),
            SeverityStat(HazardSeverity.SEDANG, 58, 40.8f),
            SeverityStat(HazardSeverity.RENDAH, 16, 11.3f)
        ),
        monthlyTrends = listOf(
            MonthlyTrend("Mei", 85, 70),
            MonthlyTrend("Jun", 98, 82),
            MonthlyTrend("Jul", 112, 95),
            MonthlyTrend("Ags", 125, 108),
            MonthlyTrend("Sep", 138, 116),
            MonthlyTrend("Okt", 142, 124)
        )
    )

    private val _dataFlow = MutableStateFlow(analyticsData)

    override fun getAnalyticsData(): Flow<AnalyticsData> = _dataFlow.asStateFlow()
}
