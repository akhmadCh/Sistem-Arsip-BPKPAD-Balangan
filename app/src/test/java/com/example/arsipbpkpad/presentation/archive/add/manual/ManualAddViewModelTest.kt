package com.example.arsipbpkpad.presentation.archive.add.manual

import com.example.arsipbpkpad.core.common.ResultState
import com.example.arsipbpkpad.domain.repository.ArchiveRepository
import com.example.arsipbpkpad.domain.usecase.SaveArchiveUseCase
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ManualAddViewModelTest {

    private val saveArchiveUseCase = mockk<SaveArchiveUseCase>()
    private val archiveRepository = mockk<ArchiveRepository>()
    private lateinit var viewModel: ManualAddViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = ManualAddViewModel(saveArchiveUseCase, archiveRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `test validation fails when fields are empty`() {
        viewModel.onEvent(ManualAddUiEvent.OnSaveClick)
        testDispatcher.scheduler.runCurrent()

        val state = viewModel.uiState.value
        assertTrue(state.validationErrors.isNotEmpty())
        assertEquals("Silakan lengkapi formulir dengan benar", state.error)
    }

    @Test
    fun `test validation fails when year is invalid`() {
        viewModel.onEvent(ManualAddUiEvent.OnDocTypeChange("SP2D"))
        viewModel.onEvent(ManualAddUiEvent.OnDocNameChange("Test Doc"))
        viewModel.onEvent(ManualAddUiEvent.OnDocNumberChange("123"))
        viewModel.onEvent(ManualAddUiEvent.OnYearChange("23")) // Invalid year
        
        viewModel.onEvent(ManualAddUiEvent.OnSaveClick)
        testDispatcher.scheduler.runCurrent()

        val state = viewModel.uiState.value
        assertEquals("Tahun harus 4 digit", state.validationErrors["year"])
    }

    @Test
    fun `test save fails when supabase error occurs`() {
        // Fill all fields
        viewModel.onEvent(ManualAddUiEvent.OnDocTypeChange("SP2D"))
        viewModel.onEvent(ManualAddUiEvent.OnDocNameChange("Name"))
        viewModel.onEvent(ManualAddUiEvent.OnDocNumberChange("NUM-123"))
        viewModel.onEvent(ManualAddUiEvent.OnDepartmentChange("Dept"))
        viewModel.onEvent(ManualAddUiEvent.OnYearChange("2024"))
        viewModel.onEvent(ManualAddUiEvent.OnValidityChange("12032024"))
        viewModel.onEvent(ManualAddUiEvent.OnNominalChange("1000"))
        viewModel.onEvent(ManualAddUiEvent.OnSubjectChange("Subject"))
        viewModel.onEvent(ManualAddUiEvent.OnWarehouseChange("Gudang"))
        viewModel.onEvent(ManualAddUiEvent.OnRackNoChange("R1"))
        viewModel.onEvent(ManualAddUiEvent.OnBoxNoChange("B1"))

        coEvery { archiveRepository.checkDocumentNumberExists(match { true }) } returns false
        coEvery { saveArchiveUseCase(match { true }) } returns ResultState.Error("Gagal mengirim ke server (tersimpan sebagai draft lokal)")

        viewModel.onEvent(ManualAddUiEvent.OnSaveClick)
        testDispatcher.scheduler.runCurrent()

        val state = viewModel.uiState.value
        assertEquals("Gagal mengirim ke server (tersimpan sebagai draft lokal)", state.error)
    }
}
