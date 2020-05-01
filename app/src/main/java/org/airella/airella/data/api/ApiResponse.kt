package org.airella.airella.data.api

import android.util.Log
import com.squareup.moshi.JsonClass
import io.reactivex.rxjava3.core.Single

@JsonClass(generateAdapter = true)
open class ApiResponse<T>(
    val data: T?,
    val errors: List<ApiError>?
)


fun <T> Single<ApiResponse<T>>.getResponse(): Single<T> =
    this.flatMap {
        return@flatMap when {
            it.data != null -> {
                Single.just(it.data)
            }
            !it.errors.isNullOrEmpty() -> {
                Single.error(ApiException(it.errors[0]))
            }
            else -> {
                Single.error(
                    ApiException(
                        "Unexpected API Error",
                        "Unexpected API Error"
                    )
                )
            }
        }
    }.doOnError {
        Log.e("airella", "Error during API request", it)
    }