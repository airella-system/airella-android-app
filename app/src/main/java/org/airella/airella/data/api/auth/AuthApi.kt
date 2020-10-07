package org.airella.airella.data.api.auth

import io.reactivex.rxjava3.core.Single
import org.airella.airella.data.api.ApiManager
import org.airella.airella.data.api.ApiResponse
import org.airella.airella.data.api.RetrofitFactory
import org.airella.airella.data.model.auth.RefreshTokenResponse
import org.airella.airella.data.model.auth.User
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {

    @POST("refresh-token")
    fun refreshToken(@Body refreshToken: String): Single<ApiResponse<RefreshTokenResponse>>

    @POST("login")
    fun login(@Body loginData: LoginData): Single<ApiResponse<User>>

    @POST("register-user")
    fun register(@Body registerData: RegisterData): Single<ApiResponse<Any>>

    companion object {

        fun create(): AuthApi {
            return RetrofitFactory
                .getBuilder(withAuthorization = false)
                .baseUrl("${ApiManager.baseApiUrl}/auth/")
                .build()
                .create(AuthApi::class.java)
        }
    }
}