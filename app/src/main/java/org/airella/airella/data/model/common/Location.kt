package org.airella.airella.data.model.common

import com.squareup.moshi.JsonClass
import java.io.Serializable

@JsonClass(generateAdapter = true)
data class Location(
    val latitude: Double,
    val longitude: Double
) : Serializable