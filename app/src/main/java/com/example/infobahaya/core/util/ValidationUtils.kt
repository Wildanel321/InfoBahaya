package com.example.infobahaya.core.util

object ValidationUtils {
    private val EMAIL_REGEX = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")

    fun isValidEmail(email: String): Boolean {
        return email.isNotBlank() && EMAIL_REGEX.matches(email.trim())
    }

    fun isValidPassword(password: String): Boolean {
        return password.trim().length >= 6
    }

    fun isValidPhone(phone: String): Boolean {
        val clean = phone.trim().filter { it.isDigit() || it == '+' }
        return clean.length >= 10
    }

    fun isValidNik(nik: String): Boolean {
        val clean = nik.trim()
        return clean.isEmpty() || (clean.length == 16 && clean.all { it.isDigit() })
    }
}
