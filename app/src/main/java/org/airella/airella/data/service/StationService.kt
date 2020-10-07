package org.airella.airella.data.service

import io.reactivex.rxjava3.core.Single
import org.airella.airella.data.api.getResponse
import org.airella.airella.data.api.isSuccess
import org.airella.airella.data.api.station.StationApi
import org.airella.airella.data.model.sensor.Statistic

object StationService {

    private val stationApi = StationApi.create()

    fun getStatistic(stationId: String, statisticId: String): Single<Statistic> =
        stationApi.getStatistic(stationId, statisticId).getResponse()

    fun removeStation(stationId: String): Single<Boolean> =
        stationApi.removeStation(stationId).isSuccess()

}