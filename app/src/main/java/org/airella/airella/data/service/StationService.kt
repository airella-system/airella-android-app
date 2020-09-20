package org.airella.airella.data.service

import io.reactivex.rxjava3.core.Single
import org.airella.airella.data.api.getResponse
import org.airella.airella.data.api.isSuccess
import org.airella.airella.data.api.station.StationApi
import org.airella.airella.data.model.sensor.Station

object StationService {

    private val stationApi = StationApi.create()

    fun getStations(): Single<List<Station>> = stationApi.getStations()
        .getResponse()


    //TODO przerobic autoryzacje
    fun removeStation(stationId: String): Single<Boolean> =
        AuthService.getAccessToken().flatMap {
            stationApi.removeStation("Bearer $it", stationId).isSuccess()
        }

}