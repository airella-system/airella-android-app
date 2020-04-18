package org.airella.airella.ui.register


/**
 * Authentication result : success (user details) or error message.
 */
sealed class RegisterResult {
    data class Success(val success: Boolean) : RegisterResult()
    data class Error(val error: Int) : RegisterResult()
}