package org.airella.airella.data.model.common

import com.squareup.moshi.JsonClass
import java.io.Serializable
import kotlin.math.max
import kotlin.math.min

@JsonClass(generateAdapter = true)
data class Location(
    val latitude: Double,
    val longitude: Double
) : Serializable {
    constructor(location: android.location.Location) : this(location.latitude, location.longitude)

    companion object {
        fun createWithValidation(latitude: Double, longitude: Double): Location =
            Location(min(max(latitude, -90.0), 90.0), min(max(longitude, -180.0), 180.0))
    }

}