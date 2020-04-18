package org.airella.airella.utils

import android.util.Patterns

object LoginUtils {
    fun isUserNameValid(username: String): Boolean {
        return username.isNotBlank()
    }

    fun isEmailValid(username: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(username).matches()
    }

    fun isPasswordValid(password: String): Boolean {
        return password.length > 5
    }

    fun isPasswordConfirmValid(password: String, passwordConfirm: String): Boolean {
        return password == passwordConfirm
    }

}
