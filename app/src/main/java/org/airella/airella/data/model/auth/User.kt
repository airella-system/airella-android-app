package org.airella.airella.data.model.auth

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class User(
    var accessToken: AccessToken? = null,
    val refreshToken: String,
    val stationRegistrationToken: String,
    var email: String = ""
)