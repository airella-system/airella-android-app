package org.airella.airella.data.api

import org.airella.airella.data.api.auth.AuthApi
import org.airella.airella.data.api.station.StationApi
import org.airella.airella.data.api.user.UserApi
import org.airella.airella.data.service.PreferencesService
import org.airella.airella.utils.Config

object ApiManager {

    var baseApiUrl: String = PreferencesService.getString("api_url", Config.DEFAULT_API_URL)
        set(value) {
            field = value
            restartApi()
        }

    lateinit var authApi: AuthApi
    lateinit var stationApi: StationApi
    lateinit var userApi: UserApi

    init {
        restartApi()
    }

    private fun restartApi() {
        authApi = AuthApi.create()
        stationApi = StationApi.create()
        userApi = UserApi.create()
    }

}