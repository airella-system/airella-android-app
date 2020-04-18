package org.airella.airella.data

import org.airella.airella.data.model.LoggedInUser
import java.io.IOException

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
object LoginDataSource {

    fun login(username: String, password: String): Result<LoggedInUser> {
        try {
            // TODO: handle loggedInUser authentication
            Thread.sleep(3000)
            if (username == "test") {
                val fakeUser = LoggedInUser(java.util.UUID.randomUUID().toString(), username)
                return Result.Success(fakeUser)
            }
            return Result.Error(IOException("Wrong login or password", null))
        } catch (e: Throwable) {
            return Result.Error(IOException("Error logging in", e))
        }
    }

    fun register(username: String, email: String, password: String): Result<Boolean> {
        try {
            // TODO: handle loggedInUser authentication
            Thread.sleep(3000)
            if (username == "test") {
                return Result.Success(true)
            }
            return Result.Success(false)
        } catch (e: Throwable) {
            return Result.Error(IOException("Error logging in", e))
        }
    }

    fun logout() {
        // TODO: revoke authentication
    }
}

