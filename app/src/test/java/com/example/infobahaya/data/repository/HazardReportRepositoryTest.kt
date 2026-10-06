package com.example.infobahaya.data.repository

import com.example.infobahaya.domain.model.HazardCategory
import com.example.infobahaya.domain.model.HazardReport
import com.example.infobahaya.domain.model.HazardSeverity
import com.example.infobahaya.domain.model.ReportStatus
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class HazardReportRepositoryTest {

    private lateinit var repository: MockHazardReportRepository

    @Before
    fun setUp() {
        repository = MockHazardReportRepository()
    }

    @Test
    fun testInitialReportsLoaded() = runTest {
        val reports = repository.getAllReports().first()
        assertTrue(reports.isNotEmpty())
        assertTrue(reports.any { it.category == HazardCategory.LISTRIK_KABEL })
    }

    @Test
    fun testFilterReportsByCategory() = runTest {
        val floodReports = repository.getReports(category = HazardCategory.BANJIR).first()
        assertTrue(floodReports.all { it.category == HazardCategory.BANJIR })
    }

    @Test
    fun testCreateReportAddsToFeed() = runTest {
        val initialCount = repository.getAllReports().first().size
        val newReport = HazardReport(
            id = "",
            title = "Jalan Ambles di Depan Pasar",
            description = "Aspal jalan amblas sedalam 30 cm membahayakan mobil dan motor",
            category = HazardCategory.JALAN_RUSAK,
            severity = HazardSeverity.TINGGI,
            status = ReportStatus.DIKIRIM,
            latitude = -6.2000,
            longitude = 106.8200,
            address = "Jl. Pasar Baru No. 12",
            createdAt = "",
            updatedAt = "",
            userId = "user_test",
            userName = "Tester"
        )

        val createResult = repository.createReport(newReport)
        assertTrue(createResult.isSuccess)
        val afterCount = repository.getAllReports().first().size
        assertEquals(initialCount + 1, afterCount)
    }

    @Test
    fun testModeratorUpdateReportStatus() = runTest {
        val firstReport = repository.getAllReports().first().first()
        val updateResult = repository.updateReportStatus(
            reportId = firstReport.id,
            newStatus = ReportStatus.SELESAI,
            moderatorNote = "Telah selesai diperbaiki oleh dinas terkait.",
            assignedDepartment = "Dinas Bina Marga"
        )
        assertTrue(updateResult.isSuccess)
        val updated = repository.getReportById(firstReport.id).first()
        assertEquals(ReportStatus.SELESAI, updated?.status)
    }

    @Test
    fun testToggleUpvote() = runTest {
        val report = repository.getAllReports().first().first()
        val initialUpvotes = report.upvotesCount
        val initialStatus = report.isUpvotedByCurrentUser

        repository.toggleUpvote(report.id)
        val updated = repository.getReportById(report.id).first()
        assertEquals(!initialStatus, updated?.isUpvotedByCurrentUser)
    }
}
