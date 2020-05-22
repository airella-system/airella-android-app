package org.airella.airella.data.model.common

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Address(
    val country: String,
    val city: String,
    val street: String,
    val number: String
)