package org.airella.airella.data.model.common

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Timespan(
    val start: String,
    val end: String
)