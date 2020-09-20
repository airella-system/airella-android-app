package org.airella.airella.data.model.common

import com.squareup.moshi.JsonClass
import java.io.Serializable

@JsonClass(generateAdapter = true)
data class Address(
    val country: String,
    val city: String,
    val street: String,
    val number: String
) : Serializable {
    override fun toString(): String {
        return "$street $number - $city - $country"
    }
}