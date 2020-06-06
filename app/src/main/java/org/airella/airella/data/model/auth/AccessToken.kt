package org.airella.airella.data.model.auth

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AccessToken(
    val token: String,
    val expirationDate: String
)