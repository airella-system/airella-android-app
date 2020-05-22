package org.airella.airella.data.model.sensor

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Sensor(
    val id: String,
    val measurements: List<Measurement>,
    val status: Int
)