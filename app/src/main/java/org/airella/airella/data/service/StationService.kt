package org.airella.airella.data.service

import io.reactivex.rxjava3.core.Single
import org.airella.airella.data.api.ApiManager
import org.airella.airella.data.api.getResponse
import org.airella.airella.data.api.isSuccess
import org.airella.airella.data.model.station.Station
import org.airella.airella.data.model.station.Statistic

object StationService {

    fun getStation(stationId: String): Single<Station> =
        ApiManager.stationApi.getStation(stationId).getResponse()

    fun getStatistic(stationId: String, statisticId: String): Single<Statistic> =
        ApiManager.stationApi.getStatistic(stationId, statisticId).getResponse()

    fun removeStation(stationId: String): Single<Boolean> =
        ApiManager.stationApi.removeStation(stationId).isSuccess()

}