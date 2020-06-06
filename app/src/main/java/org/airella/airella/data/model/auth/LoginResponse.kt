package org.airella.airella.data.model.auth

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LoginResponse(
    val accessToken: AccessToken? = null,
    val refreshToken: String,
    val stationRegistrationToken: String,
    var email: String = ""
)