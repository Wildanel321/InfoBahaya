package com.example.infobahaya.domain.model

data class CategoryStat(
    val category: HazardCategory,
    val count: Int,
    val percentage: Float
)

data class SeverityStat(
    val severity: HazardSeverity,
    val count: Int,
    val percentage: Float
)

data class MonthlyTrend(
    val monthLabel: String,
    val reportsCount: Int,
    val resolvedCount: Int
)

data class AnalyticsData(
    val totalReports: Int,
    val newReportsCount: Int,
    val inProgressCount: Int,
    val resolvedCount: Int,
    val priorityCount: Int,
    val resolutionRatePercentage: Float,
    val averageResponseTimeHours: Float,
    val categoryBreakdown: List<CategoryStat>,
    val severityBreakdown: List<SeverityStat>,
    val monthlyTrends: List<MonthlyTrend>
)
