package org.airella.airella.data.service


import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import org.airella.airella.data.api.auth.AuthApi
import org.airella.airella.data.api.auth.LoginData
import org.airella.airella.data.api.auth.RegisterData
import org.airella.airella.data.api.getResponse
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
        val username = PreferencesService.getString("username", "")
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

    fun login(username: String, password: String): Single<LoginResponse> {
        user?.let { return Single.just(it) }

        return loginApi.login(
            LoginData(
                username,
                password
            )
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .getResponse()
            .map { it.apply { this.username = username } }
            .doOnSuccess {
                setLoggedInUser(it)
                Log.i("Login successful user: " + it.username)
            }
    }

    fun register(username: String, email: String, password: String): Single<Boolean> {
        return loginApi.register(
            RegisterData(
                username,
                email,
                password
            )
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .getResponse()
            .map { true }
    }

    private fun setLoggedInUser(loginResponse: LoginResponse) {
        user = loginResponse
        PreferencesService.putString("username", loginResponse.username)
        PreferencesService.putString("refreshToken", loginResponse.refreshToken)
        PreferencesService.putString(
            "stationRegistrationToken",
            loginResponse.stationRegistrationToken
        )
    }
}
