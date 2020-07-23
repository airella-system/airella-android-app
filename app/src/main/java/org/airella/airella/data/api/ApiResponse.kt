package org.airella.airella.data.api

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import org.airella.airella.utils.Log
import retrofit2.HttpException

@JsonClass(generateAdapter = true)
open class ApiResponse<T>(
    val success: Boolean,
    val data: T?,
    val errors: List<ApiError>?
)


fun Single<ApiResponse<Any>>.isSuccess(): Single<Boolean> =
    this.subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .onErrorResumeNext {
            if (it is HttpException) {
                try {
                    val type = Types.newParameterizedType(ApiResponse::class.java, Any::class.java)
                    val adapter: JsonAdapter<ApiResponse<Any>> =
                        Moshi.Builder().build().adapter(type)
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
                        ApiException(
                            "",
                            "Unexpected API Error",
                            "Unexpected API Error"
                        )
                    )
                }
            }
        }.doOnError {
            Log.w("Error during API request: [$it]")
        }

inline fun <reified T> Single<ApiResponse<T>>.getResponse(): Single<T> =
    this.subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .onErrorResumeNext {
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
                            "",
                            "Unexpected API Error",
                            "Unexpected API Error"
                        )
                    )
                }
            }
        }.doOnError {
            Log.w("Error during API request: [$it]")
        }