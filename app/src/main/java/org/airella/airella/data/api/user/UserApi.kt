package org.airella.airella.data.api.user

import io.reactivex.rxjava3.core.Single
import org.airella.airella.data.api.ApiResponse
import org.airella.airella.data.api.RetrofitFactory
import org.airella.airella.data.model.sensor.Station
import retrofit2.Retrofit
import retrofit2.http.GET

interface UserApi {

    @GET("user/stations/")
    fun getUserStations(): Single<ApiResponse<List<Station>>>

    companion object {

        private val retrofit: Retrofit by lazy {
            RetrofitFactory.getBuilder(withAuthorization = true)
                .build()
        }

        fun create(): UserApi {
            return retrofit.create(
                UserApi::class.java
            )
        }
    }

}