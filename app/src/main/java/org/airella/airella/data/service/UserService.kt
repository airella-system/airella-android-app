package org.airella.airella.data.service

import io.reactivex.rxjava3.core.Single
import org.airella.airella.data.api.getResponse
import org.airella.airella.data.api.user.UserApi
import org.airella.airella.data.model.sensor.Station

object UserService {

    private val userApi = UserApi.create()

    //TODO przerobic autoryzacje
    fun getUserStations(): Single<List<Station>> =
        AuthService.getAccessToken().flatMap {
            userApi.getUserStations("Bearer $it").getResponse()
        }

}