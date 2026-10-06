package com.example.infobahaya.data.repository

import com.example.infobahaya.domain.model.UserRole
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class AuthRepositoryTest {

    private lateinit var authRepository: MockAuthRepository

    @Before
    fun setUp() {
        authRepository = MockAuthRepository()
    }

    @Test
    fun testInitialSessionIsAvailable() = runTest {
        val user = authRepository.getCurrentUser().first()
        assertNotNull(user)
        assertEquals("user_101", user?.id)
        assertTrue(authRepository.hasActiveSession())
    }

    @Test
    fun testLoginCitizenSuccess() = runTest {
        val result = authRepository.login("rian.pratama@gmail.com", "password123")
        assertTrue(result.isSuccess)
        val user = result.getOrNull()
        assertEquals(UserRole.CITIZEN, user?.role)
    }

    @Test
    fun testLoginModeratorRoleAutoDetected() = runTest {
        val result = authRepository.login("moderator.budi@infobahaya.go.id", "password123")
        assertTrue(result.isSuccess)
        val user = result.getOrNull()
        assertEquals(UserRole.MODERATOR, user?.role)
    }

    @Test
    fun testLoginEmptyEmailFails() = runTest {
        val result = authRepository.login("", "password123")
        assertTrue(result.isFailure)
    }

    @Test
    fun testLogoutClearsSession() = runTest {
        authRepository.logout()
        val user = authRepository.getCurrentUser().first()
        assertEquals(null, user)
    }
}
