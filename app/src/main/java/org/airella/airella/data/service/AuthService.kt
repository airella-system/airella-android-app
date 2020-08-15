package org.airella.airella.data.service


import io.reactivex.rxjava3.core.Single
import org.airella.airella.data.api.auth.AuthApi
import org.airella.airella.data.api.auth.LoginData
import org.airella.airella.data.api.auth.RegisterData
import org.airella.airella.data.api.getResponse
import org.airella.airella.data.api.isSuccess
import org.airella.airella.data.model.auth.LoginResponse
import org.airella.airella.utils.Log

/**
 * Class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and user credentials information.
 */

object AuthService {

    private val loginApi = AuthApi.create()

    var user: LoginResponse? = null
        private set

    init {
        val username = PreferencesService.getString("email", "")
        val refreshToken = PreferencesService.getString("refreshToken", "")
        val stationRegistrationToken = PreferencesService.getString("stationRegistrationToken", "")

        if (username.isNotEmpty() && refreshToken.isNotEmpty() && stationRegistrationToken.isNotEmpty()) {
            user = LoginResponse(null, refreshToken, stationRegistrationToken, username)
        }
    }

    fun isLoggedIn(): Boolean = user != null

    fun logout() {
        user = null
        PreferencesService.remove("username")
        PreferencesService.remove("refreshToken")
        PreferencesService.remove("stationRegistrationToken")
    }

    fun login(email: String, password: String): Single<LoginResponse> {
        user?.let { return Single.just(it) }

        return loginApi.login(
            LoginData(
                email,
                password
            )
        )
            .getResponse()
            .map { it.apply { this.email = email } }
            .doOnSuccess {
                setLoggedInUser(it)
                Log.i("Login successful user: " + it.email)
            }
    }

    fun register(email: String, password: String): Single<Boolean> {
        return loginApi.register(RegisterData(email, password))
            .isSuccess()
            .map { true }
    }

    private fun setLoggedInUser(loginResponse: LoginResponse) {
        user = loginResponse
        PreferencesService.putString("email", loginResponse.email)
        PreferencesService.putString("refreshToken", loginResponse.refreshToken)
        PreferencesService.putString(
            "stationRegistrationToken",
            loginResponse.stationRegistrationToken
        )
    }
}
