package org.airella.airella.data.api.auth

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RegisterData(val email: String, val password: String)