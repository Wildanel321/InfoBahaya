package com.example.infobahaya.presentation.auth

import com.example.infobahaya.data.repository.MockAuthRepository
import com.example.infobahaya.domain.model.UserRole
import com.example.infobahaya.testutils.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var authRepository: MockAuthRepository
    private lateinit var viewModel: LoginViewModel

    @Before
    fun setUp() {
        authRepository = MockAuthRepository()
        viewModel = LoginViewModel(authRepository)
    }

    @Test
    fun testEmptyEmailValidation() = runTest {
        viewModel.onEmailChange("")
        viewModel.onPasswordChange("password123")
        viewModel.login()

        val state = viewModel.uiState.value
        assertEquals("Email wajib diisi", state.emailError)
    }

    @Test
    fun testInvalidEmailFormat() = runTest {
        viewModel.onEmailChange("invalid-email")
        viewModel.onPasswordChange("password123")
        viewModel.login()

        val state = viewModel.uiState.value
        assertEquals("Format email tidak valid", state.emailError)
    }

    @Test
    fun testShortPasswordValidation() = runTest {
        viewModel.onEmailChange("user@infobahaya.go.id")
        viewModel.onPasswordChange("123")
        viewModel.login()

        val state = viewModel.uiState.value
        assertEquals("Password minimal 6 karakter", state.passwordError)
    }

    @Test
    fun testSuccessfulLogin() = runTest {
        viewModel.onEmailChange("rian.pratama@gmail.com")
        viewModel.onPasswordChange("password123")
        viewModel.login()

        val state = viewModel.uiState.value
        assertTrue(state.isSuccess)
        assertNotNull(state.loggedInUser)
        assertEquals(UserRole.CITIZEN, state.loggedInUser?.role)
        assertNull(state.generalErrorMessage)
    }

    @Test
    fun testFillDemoAccountModerator() {
        viewModel.fillDemoAccount(isModerator = true)
        val state = viewModel.uiState.value
        assertEquals("moderator.budi@infobahaya.go.id", state.email)
        assertEquals("password123", state.password)
    }
}
