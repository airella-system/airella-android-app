package org.airella.airella.data.service

import io.reactivex.rxjava3.core.Single
import org.airella.airella.data.api.isSuccess
import org.airella.airella.data.api.station.StationApi

object StationService {

    private val stationApi = StationApi.create()

    //TODO przerobic autoryzacje
    fun removeStation(stationId: String): Single<Boolean> =
        stationApi.removeStation(stationId).isSuccess()

}