package org.airella.airella.ui.register

import org.airella.airella.data.model.LoggedInUser

/**
 * Authentication result : success (user details) or error message.
 */
data class RegisterResult(
    val success: LoggedInUser? = null,
    val error: Int? = null
)
