package com.example.infobahaya.core.util

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ValidationUtilsTest {

    @Test
    fun testValidEmails() {
        assertTrue(ValidationUtils.isValidEmail("user@example.com"))
        assertTrue(ValidationUtils.isValidEmail("john.doe@sub.domain.co.id"))
        assertTrue(ValidationUtils.isValidEmail("moderator.budi@infobahaya.go.id"))
    }

    @Test
    fun testInvalidEmails() {
        assertFalse(ValidationUtils.isValidEmail(""))
        assertFalse(ValidationUtils.isValidEmail("invalid-email"))
        assertFalse(ValidationUtils.isValidEmail("@missingusername.com"))
        assertFalse(ValidationUtils.isValidEmail("user@.com"))
    }

    @Test
    fun testValidPassword() {
        assertTrue(ValidationUtils.isValidPassword("password123"))
        assertTrue(ValidationUtils.isValidPassword("123456"))
    }

    @Test
    fun testInvalidPassword() {
        assertFalse(ValidationUtils.isValidPassword("12345"))
        assertFalse(ValidationUtils.isValidPassword(""))
    }

    @Test
    fun testPhoneValidation() {
        assertTrue(ValidationUtils.isValidPhone("081234567890"))
        assertTrue(ValidationUtils.isValidPhone("+628123456789"))
        assertFalse(ValidationUtils.isValidPhone("08123"))
    }

    @Test
    fun testNikValidation() {
        assertTrue(ValidationUtils.isValidNik("")) // Optional
        assertTrue(ValidationUtils.isValidNik("3171012345670001"))
        assertFalse(ValidationUtils.isValidNik("317101234567"))
        assertFalse(ValidationUtils.isValidNik("317101234567000A"))
    }
}
