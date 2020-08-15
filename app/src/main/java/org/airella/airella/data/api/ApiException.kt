package org.airella.airella.data.api

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ApiError(
    val status: String,
    val title: String,
    val detail: String
)

class ApiException(
    val status: String,
    val error: String,
    override val message: String
) : Throwable(message) {
    constructor(apiError: ApiError) : this(apiError.status, apiError.title, apiError.detail)
}