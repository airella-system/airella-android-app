package org.airella.airella.utils

import android.content.Context
import android.content.Intent
import android.util.Patterns
import org.airella.airella.ui.login.LoginActivity

object AuthUtils {

    fun goToLoginPage(context: Context) {
        val intent = Intent(context, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(intent)
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
