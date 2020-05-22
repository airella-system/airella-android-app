package org.airella.airella.data.api.station

import io.reactivex.rxjava3.core.Single
import org.airella.airella.data.api.ApiResponse
import org.airella.airella.data.api.RetrofitFactory
import org.airella.airella.data.model.sensor.Station
import retrofit2.Retrofit
import retrofit2.http.GET

interface StationApi {

    @GET("stations/")
    fun getStations(): Single<ApiResponse<List<Station>>>

    companion object {

        private val retrofit: Retrofit by lazy {
            RetrofitFactory.getBuilder()
                .baseUrl(RetrofitFactory.baseUrl)
                .build()
        }

        fun create(): StationApi {
            return retrofit.create(
                StationApi::class.java
            )
        }
    }

}