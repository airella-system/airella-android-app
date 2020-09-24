package org.airella.airella.data.service


import io.reactivex.rxjava3.core.Single
import org.airella.airella.data.api.auth.AuthApi
import org.airella.airella.data.api.auth.LoginData
import org.airella.airella.data.api.auth.RegisterData
import org.airella.airella.data.api.getResponse
import org.airella.airella.data.api.isSuccess
import org.airella.airella.data.model.auth.AccessToken
import org.airella.airella.data.model.auth.User
import org.airella.airella.exception.UserNotLoggedException
import org.airella.airella.utils.Log
import org.airella.airella.utils.RxUtils.runAsync

/**
 * Class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and user credentials information.
 */

object AuthService {

    private val authApi = AuthApi.create()

    private var user: User? = null

    @Throws(UserNotLoggedException::class)
    fun getUser(): User = user ?: throw UserNotLoggedException()

    fun getAccessToken(): Single<String> =
        getUser().accessToken?.token?.let { Single.just(it) }
            ?: refreshToken().map { it.token }.runAsync()

    fun clearAccessToken() {
        getUser().accessToken = null
    }

    init {
        val email = PreferencesService.getString("email", "")
        val refreshToken = PreferencesService.getString("refreshToken", "")
        val stationRegistrationToken = PreferencesService.getString("stationRegistrationToken", "")

        if (email.isNotEmpty() && refreshToken.isNotEmpty() && stationRegistrationToken.isNotEmpty()) {
            user = User(null, refreshToken, stationRegistrationToken, email)
        }
    }

    fun isUserLogged(): Boolean = user != null

    fun logout() {
        user = null
        PreferencesService.remove("email")
        PreferencesService.remove("refreshToken")
        PreferencesService.remove("stationRegistrationToken")
    }

    fun refreshToken(): Single<AccessToken> {
        clearAccessToken()
        return try {
            authApi.refreshToken(getUser().refreshToken).getResponse()
                .map { it.accessToken }
                .doOnSuccess { getUser().accessToken = it }
        } catch (e: UserNotLoggedException) {
            Single.error(e)
        }
    }

    fun login(email: String, password: String): Single<User> {
        return authApi.login(
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
        return authApi.register(RegisterData(email, password))
            .isSuccess()
            .map { true }
    }

    private fun setLoggedInUser(user: User) {
        this.user = user
        PreferencesService.putString("email", user.email)
        PreferencesService.putString("refreshToken", user.refreshToken)
        PreferencesService.putString(
            "stationRegistrationToken",
            user.stationRegistrationToken
        )
    }
}
