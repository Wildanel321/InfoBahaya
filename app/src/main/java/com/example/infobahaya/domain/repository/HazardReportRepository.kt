package com.example.infobahaya.domain.repository

import com.example.infobahaya.domain.model.DraftReport
import com.example.infobahaya.domain.model.HazardCategory
import com.example.infobahaya.domain.model.HazardReport
import com.example.infobahaya.domain.model.HazardSeverity
import com.example.infobahaya.domain.model.ReportStatus
import kotlinx.coroutines.flow.Flow

interface HazardReportRepository {
    fun getAllReports(): Flow<List<HazardReport>>
    fun getReports(
        category: HazardCategory? = null,
        severity: HazardSeverity? = null,
        status: ReportStatus? = null,
        searchQuery: String = ""
    ): Flow<List<HazardReport>>
    fun getReportById(id: String): Flow<HazardReport?>
    fun getUserReports(userId: String): Flow<List<HazardReport>>
    fun getDraft(): Flow<DraftReport?>
    suspend fun saveDraft(draft: DraftReport)
    suspend fun clearDraft()
    suspend fun createReport(report: HazardReport): Result<HazardReport>
    suspend fun toggleUpvote(reportId: String): Result<HazardReport>
    suspend fun updateReportStatus(
        reportId: String,
        newStatus: ReportStatus,
        moderatorNote: String,
        assignedDepartment: String? = null
    ): Result<HazardReport>
    suspend fun syncPendingReports(): Result<Int>
    fun getOfflinePendingCount(): Flow<Int>
    fun isOfflineMode(): Flow<Boolean>
    suspend fun setOfflineMode(isOffline: Boolean)
}
