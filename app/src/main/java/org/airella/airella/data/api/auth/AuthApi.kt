package org.airella.airella.data.api.auth

import io.reactivex.rxjava3.core.Single
import org.airella.airella.data.api.ApiResponse
import org.airella.airella.data.api.RetrofitFactory
import org.airella.airella.data.model.auth.LoginResponse
import retrofit2.Retrofit
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {

    @POST("login")
    fun login(@Body loginData: LoginData): Single<ApiResponse<LoginResponse>>

    @POST("register-user")
    fun register(@Body registerData: RegisterData): Single<ApiResponse<LoginResponse>>

    companion object {

        private val retrofit: Retrofit by lazy {
            RetrofitFactory.getBuilder()
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