package org.airella.airella.data.model.sensor

import com.squareup.moshi.JsonClass
import org.airella.airella.data.model.common.Timespan
import java.io.Serializable

@JsonClass(generateAdapter = true)
data class Measurement(
    val timespan: Timespan,
    val timestamp: String,
    val value: Double
) : Serializable