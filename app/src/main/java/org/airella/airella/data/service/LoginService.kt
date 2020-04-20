package org.airella.airella.data.service


import io.reactivex.rxjava3.core.Single
import org.airella.airella.data.api.auth.AuthApi
import org.airella.airella.data.api.auth.LoginData
import org.airella.airella.data.api.auth.RegisterData
import org.airella.airella.data.api.getResponse
import org.airella.airella.data.model.LoginResponse

/**
 * Class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and user credentials information.
 */

object LoginService {

    private val loginApi = AuthApi.create()

    var user: LoginResponse? = null
        private set

    val isLoggedIn: Boolean
        get() = user != null

    init {
        // If user credentials will be cached in local storage, it is recommended it be encrypted
        // @see https://developer.android.com/training/articles/keystore
        user = null
    }

    fun logout() {
        user = null
    }

    fun login(username: String, password: String): Single<LoginResponse> {
        user?.let { return Single.just(it) }

        val result = loginApi.login(LoginData(username, password))
            .getResponse()
            .map { it.apply { this.username = username } }

        result.doOnSuccess {
            setLoggedInUser(it)
        }

        return result
    }

    fun register(username: String, email: String, password: String): Single<Boolean> {
        return loginApi.register(RegisterData(username, email, password))
            .getResponse()
            .flatMap {
                Single.just(true)
            }
    }

    private fun setLoggedInUser(loginResponse: LoginResponse) {
        user = loginResponse
        // If user credentials will be cached in local storage, it is recommended it be encrypted
        // @see https://developer.android.com/training/articles/keystore
    }
}
