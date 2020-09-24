package org.airella.airella.data.api.station

import io.reactivex.rxjava3.core.Single
import org.airella.airella.data.api.ApiResponse
import org.airella.airella.data.api.RetrofitFactory
import retrofit2.Retrofit
import retrofit2.http.DELETE
import retrofit2.http.Path

interface StationApi {

    @DELETE("stations/{stationId}")
    fun removeStation(@Path("stationId") stationId: String): Single<ApiResponse<Any>>


    companion object {

        private val retrofit: Retrofit by lazy {
            RetrofitFactory.getBuilder(withAuthorization = true)
                .build()
        }

        fun create(): StationApi {
            return retrofit.create(
                StationApi::class.java
            )
        }
    }

}