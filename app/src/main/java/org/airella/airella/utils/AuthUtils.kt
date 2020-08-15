package org.airella.airella.utils

import android.util.Patterns

object AuthUtils {
    fun isUserNameValid(username: String): Boolean {
        return username.isNotBlank()
    }

    fun isEmailValid(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun isPasswordValid(password: String): Boolean {
        return password.length >= 6
    }

    fun isPasswordConfirmValid(password: String, passwordConfirm: String): Boolean {
        return password == passwordConfirm
    }

}
