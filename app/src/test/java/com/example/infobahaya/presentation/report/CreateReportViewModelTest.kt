package com.example.infobahaya.presentation.report

import com.example.infobahaya.data.repository.MockAuthRepository
import com.example.infobahaya.data.repository.MockHazardReportRepository
import com.example.infobahaya.domain.model.HazardCategory
import com.example.infobahaya.domain.model.HazardSeverity
import com.example.infobahaya.testutils.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CreateReportViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var authRepository: MockAuthRepository
    private lateinit var hazardRepository: MockHazardReportRepository
    private lateinit var viewModel: CreateReportViewModel

    @Before
    fun setUp() {
        authRepository = MockAuthRepository()
        hazardRepository = MockHazardReportRepository()
        viewModel = CreateReportViewModel(hazardRepository, authRepository)
    }

    @Test
    fun testStep1ValidationRequiresCategory() = runTest {
        viewModel.nextStep()
        assertEquals("Pilih kategori bahaya terlebih dahulu", viewModel.uiState.value.stepError)

        viewModel.onSelectCategory(HazardCategory.JALAN_RUSAK)
        viewModel.nextStep()
        assertEquals(2, viewModel.uiState.value.currentStep)
    }

    @Test
    fun testFull7StepReportingFlow() = runTest {
        // Step 1: Category
        viewModel.onSelectCategory(HazardCategory.POHON_TUMBANG)
        viewModel.nextStep()
        assertEquals(2, viewModel.uiState.value.currentStep)

        // Step 2: Photo
        viewModel.addSamplePhoto("https://images.unsplash.com/photo-tree")
        viewModel.nextStep()
        assertEquals(3, viewModel.uiState.value.currentStep)

        // Step 3: Location
        viewModel.setLocation(-6.2088, 106.8456, "Jl. Sudirman No. 1", "Dekat Halte Busway")
        viewModel.nextStep()
        assertEquals(4, viewModel.uiState.value.currentStep)

        // Step 4: Severity
        viewModel.onSelectSeverity(HazardSeverity.TINGGI)
        viewModel.nextStep()
        assertEquals(5, viewModel.uiState.value.currentStep)

        // Step 5: Description
        viewModel.onTitleChange("Pohon Tumbang Menutup Jalur Cepat")
        viewModel.onDescriptionChange("Pohon beringin besar roboh menutup seluruh badan jalan arah Bundaran HI")
        viewModel.nextStep()
        assertEquals(6, viewModel.uiState.value.currentStep)

        // Step 6: Review & Submit -> Step 7: Success
        viewModel.nextStep()
        val state = viewModel.uiState.value
        assertEquals(7, state.currentStep)
        assertTrue(state.isSubmitted)
        assertNotNull(state.createdReport)
    }

    @Test
    fun testDiscardDraft() = runTest {
        viewModel.onSelectCategory(HazardCategory.BANJIR)
        viewModel.discardDraft()
        assertEquals(null, viewModel.uiState.value.category)
        assertEquals(1, viewModel.uiState.value.currentStep)
    }
}
