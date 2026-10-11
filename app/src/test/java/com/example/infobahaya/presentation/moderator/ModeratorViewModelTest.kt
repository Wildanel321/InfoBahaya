package com.example.infobahaya.presentation.moderator

import com.example.infobahaya.data.repository.MockAnalyticsRepository
import com.example.infobahaya.data.repository.MockHazardReportRepository
import com.example.infobahaya.domain.model.HazardCategory
import com.example.infobahaya.domain.model.HazardSeverity
import com.example.infobahaya.domain.model.ReportStatus
import com.example.infobahaya.testutils.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ModeratorViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var hazardRepository: MockHazardReportRepository
    private lateinit var analyticsRepository: MockAnalyticsRepository
    private lateinit var viewModel: ModeratorViewModel

    @Before
    fun setUp() {
        hazardRepository = MockHazardReportRepository()
        analyticsRepository = MockAnalyticsRepository()
        viewModel = ModeratorViewModel(hazardRepository, analyticsRepository)
    }

    @Test
    fun testInitialQueueAndKpiCounts() = runTest {
        advanceUntilIdle()
        val state = viewModel.uiState.value
        assertTrue(state.rawReports.isNotEmpty())
        assertEquals(state.rawReports.size, state.filteredReportsQueue.size)
        assertTrue(state.pendingCount >= 0)
        assertTrue(state.inProgressCount >= 0)
        assertNotNull(state.analytics)
    }

    @Test
    fun testFilterByPendingTab() = runTest {
        advanceUntilIdle()
        viewModel.setFilterTab(ModeratorFilterTab.PENDING)
        val state = viewModel.uiState.value
        assertEquals(ModeratorFilterTab.PENDING, state.activeFilterTab)
        state.filteredReportsQueue.forEach { report ->
            assertTrue(report.status == ReportStatus.DIKIRIM || report.status == ReportStatus.MENUNGGU_VERIFIKASI)
        }
    }

    @Test
    fun testSearchQueryFilter() = runTest {
        advanceUntilIdle()
        viewModel.onSearchQueryChange("REP-2026-001")
        val state = viewModel.uiState.value
        assertEquals(1, state.filteredReportsQueue.size)
        assertEquals("REP-2026-001", state.filteredReportsQueue.first().id)
    }

    @Test
    fun testQuickApprove() = runTest {
        advanceUntilIdle()
        val firstReport = viewModel.uiState.value.rawReports.first()
        viewModel.quickApprove(firstReport)
        advanceUntilIdle()

        val updated = hazardRepository.getReportById(firstReport.id).first()
        assertEquals(ReportStatus.DIVERIFIKASI, updated?.status)
        assertNotNull(viewModel.uiState.value.feedbackMessage)
    }

    @Test
    fun testQuickReject() = runTest {
        advanceUntilIdle()
        val firstReport = viewModel.uiState.value.rawReports.first()
        viewModel.quickReject(firstReport, "Foto tidak sesuai lokasi")
        advanceUntilIdle()

        val updated = hazardRepository.getReportById(firstReport.id).first()
        assertEquals(ReportStatus.DITOLAK, updated?.status)
    }

    @Test
    fun testSubmitFullModerationDecision() = runTest {
        advanceUntilIdle()
        val firstReport = viewModel.uiState.value.rawReports.first()
        viewModel.openActionDialog(firstReport)
        assertEquals(firstReport, viewModel.uiState.value.selectedReportForAction)

        viewModel.submitModerationDecision(
            newStatus = ReportStatus.DIPROSES,
            note = "Tim PLN bergerak ke lokasi",
            assignedDept = "PLN UID Jakarta Raya",
            newSeverity = HazardSeverity.DARURAT
        )
        advanceUntilIdle()

        val updated = hazardRepository.getReportById(firstReport.id).first()
        assertEquals(ReportStatus.DIPROSES, updated?.status)
        assertEquals(HazardSeverity.DARURAT, updated?.severity)
        assertEquals("PLN UID Jakarta Raya", updated?.assignedDepartment)
    }
}
