package org.airella.airella.data.api.auth

import io.reactivex.rxjava3.core.Single
import org.airella.airella.data.api.ApiResponse
import org.airella.airella.data.api.RetrofitFactory
import org.airella.airella.data.model.auth.RefreshTokenResponse
import org.airella.airella.data.model.auth.User
import retrofit2.Retrofit
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

        private val retrofit: Retrofit by lazy {
            RetrofitFactory.getBuilder(withAuthorization = false)
                .baseUrl(RetrofitFactory.baseUrl + "auth/")
                .build()
        }

        fun create(): AuthApi {
            return retrofit.create(
                AuthApi::class.java
            )
        }
    }
}