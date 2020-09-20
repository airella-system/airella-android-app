package org.airella.airella.data.model.sensor

import com.squareup.moshi.JsonClass
import org.airella.airella.data.model.common.Address
import org.airella.airella.data.model.common.Location
import java.io.Serializable

@JsonClass(generateAdapter = true)
data class Station(
    val id: String,
    val name: String?,
    val address: Address?,
    val location: Location?,
    val aqi: Double?,
    val sensors: List<Sensor>?
) : Serializable