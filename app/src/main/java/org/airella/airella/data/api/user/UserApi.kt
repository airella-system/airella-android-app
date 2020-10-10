package org.airella.airella.data.api.user

import io.reactivex.rxjava3.core.Single
import org.airella.airella.data.api.ApiResponse
import org.airella.airella.data.api.RetrofitFactory
import org.airella.airella.data.model.station.Station
import retrofit2.http.GET

interface UserApi {

    @GET("user/stations/")
    fun getUserStations(): Single<ApiResponse<List<Station>>>

    companion object {

        fun create(): UserApi {
            return RetrofitFactory
                .getBuilder(withAuthorization = true)
                .build()
                .create(UserApi::class.java)
        }
    }

}