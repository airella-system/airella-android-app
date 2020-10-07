package org.airella.airella.data.model.sensor

import com.squareup.moshi.JsonClass
import java.io.Serializable
import java.time.LocalDateTime

@JsonClass(generateAdapter = true)
data class Statistic(
    val id: String,
    val name: String?,
    val type: String?,
    val privacyMode: String?,
    val values: List<StatisticValue>
) : Serializable


@JsonClass(generateAdapter = true)
data class StatisticValue(
    val timestamp: LocalDateTime,
    val value: String
) : Serializable