package org.airella.airella.data.model

import com.squareup.moshi.JsonClass
import org.airella.airella.data.api.auth.AccessToken

@JsonClass(generateAdapter = true)
data class LoginResponse(
    val accessToken: AccessToken,
    val refreshToken: String,
    val stationRegistrationToken: String
) {
    var username: String? = null
}