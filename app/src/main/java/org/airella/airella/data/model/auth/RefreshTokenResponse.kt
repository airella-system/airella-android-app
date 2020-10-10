package org.airella.airella.data.model.auth

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RefreshTokenResponse(
    val accessToken: AccessToken
)