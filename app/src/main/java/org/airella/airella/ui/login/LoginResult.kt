package org.airella.airella.ui.login

import org.airella.airella.data.model.LoggedInUser

/**
 * Authentication result : success (user details) or error message.
 */
sealed class LoginResult {
    data class Success(val user: LoggedInUser) : LoginResult()
    data class Error(val error: Int) : LoginResult()
}