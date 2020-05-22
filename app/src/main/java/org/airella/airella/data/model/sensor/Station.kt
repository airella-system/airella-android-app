package org.airella.airella.data.model.sensor

import com.squareup.moshi.JsonClass
import org.airella.airella.data.model.common.Address
import org.airella.airella.data.model.common.Location

@JsonClass(generateAdapter = true)
data class Station(
    val id: Int,
    val name: String,
    val address: Address?,
    val location: Location?,
    val caqi: Int?,
    val sensors: List<Sensor>?
)