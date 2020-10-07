package org.airella.airella.data.service

import io.reactivex.rxjava3.core.Single
import org.airella.airella.data.api.ApiManager
import org.airella.airella.data.api.getResponse
import org.airella.airella.data.model.sensor.Station

object UserService {

    fun getUserStations(): Single<List<Station>> =
        ApiManager.userApi.getUserStations().getResponse()

}