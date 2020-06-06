package org.airella.airella.data.api

import com.squareup.moshi.JsonClass
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import org.airella.airella.utils.Log

@JsonClass(generateAdapter = true)
open class ApiResponse<T>(
    val success: Boolean,
    val data: T?,
    val errors: List<ApiError>?
)


fun <T> Single<ApiResponse<T>>.getResponse(): Single<T> =
    this.subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .flatMap {
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
            Log.w("Error during API request: ${it.message}")
    }