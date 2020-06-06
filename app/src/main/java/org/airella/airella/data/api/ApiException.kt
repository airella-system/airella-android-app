package org.airella.airella.data.api

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ApiError(
    val error: String,
    val message: String
)

class ApiException(
    val error: String,
    message: String
) : Throwable(message) {
    constructor(apiError: ApiError) : this(apiError.error, apiError.message)
}