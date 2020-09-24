package org.airella.airella.data.api

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import io.reactivex.rxjava3.core.Single
import org.airella.airella.data.service.AuthService
import org.airella.airella.utils.Log
import org.airella.airella.utils.RxUtils.runAsync
import retrofit2.HttpException

@JsonClass(generateAdapter = true)
open class ApiResponse<T>(
    val success: Boolean,
    val data: T?,
    val errors: List<ApiError>?
)


fun Single<ApiResponse<Any>>.isSuccess(): Single<Boolean> {
    return this.runAsync()
        .errorToResponse()
        .flatMap {
            return@flatMap when {
                !it.errors.isNullOrEmpty() -> {
                    Single.error(ApiException(it.errors[0]))
                }
                it.success -> {
                    Single.just(true)
                }
                else -> {
                    Single.error(
                        ApiException("", "Unexpected API Error", "Unexpected API Error")
                    )
                }
            }
        }.doOnError {
            Log.w("Error during API request: [$it]")
        }
}

inline fun <reified T> Single<ApiResponse<T>>.getResponse(): Single<T> {
    return this.runAsync()
        .errorToResponse()
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
                        ApiException("", "Unexpected API Error", "Unexpected API Error")
                    )
                }
            }
        }.retry(2) { throwable ->
            isWrongAccessToken(throwable).also { if (it) AuthService.clearAccessToken() }
        }
}

inline fun <reified T> Single<ApiResponse<T>>.errorToResponse(): Single<ApiResponse<T>> {
    return this.onErrorResumeNext {
        if (it is HttpException) {
            try {
                val type = Types.newParameterizedType(ApiResponse::class.java, T::class.java)
                val adapter: JsonAdapter<ApiResponse<T>> = Moshi.Builder().build().adapter(type)
                val response =
                    it.response()?.errorBody()?.string()?.let { body -> adapter.fromJson(body) }
                if (response != null) {
                    return@onErrorResumeNext Single.just(response)
                }
            } catch (_: Throwable) {
            }
        }
        return@onErrorResumeNext Single.error(it)
    }
}

fun isWrongAccessToken(throwable: Throwable): Boolean =
    throwable is ApiException && (throwable.status == "400" || throwable.status == "403")