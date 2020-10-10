package org.airella.airella.data.api.station

import io.reactivex.rxjava3.core.Single
import org.airella.airella.data.api.ApiResponse
import org.airella.airella.data.api.RetrofitFactory
import org.airella.airella.data.model.station.Statistic
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Path

interface StationApi {

    @GET("stations/{stationId}/statistics/{statisticId}")
    fun getStatistic(
        @Path("stationId") stationId: String,
        @Path("statisticId") statisticId: String
    ): Single<ApiResponse<Statistic>>

    @DELETE("stations/{stationId}")
    fun removeStation(@Path("stationId") stationId: String): Single<ApiResponse<Any>>


    companion object {

        fun create(): StationApi {
            return RetrofitFactory
                .getBuilder(withAuthorization = true)
                .build()
                .create(StationApi::class.java)
        }
    }

}