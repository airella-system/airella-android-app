package org.airella.airella.data.api

import com.squareup.moshi.Moshi
import hu.akarnokd.rxjava3.retrofit.RxJava3CallAdapterFactory
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import org.airella.airella.data.service.AuthService
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit


object RetrofitFactory {

    const val baseUrl = "http://airella.cyfrogen.com/api/"

    fun getHttpClientBuilder(isAuthorization: Boolean): OkHttpClient.Builder {
        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)
        val httpClientBuilder = OkHttpClient.Builder()
        httpClientBuilder.addNetworkInterceptor(logging)
        httpClientBuilder.readTimeout(15, TimeUnit.SECONDS)
        httpClientBuilder.connectTimeout(15, TimeUnit.SECONDS)

        if (isAuthorization) {
            httpClientBuilder.addInterceptor(object : Interceptor {
                override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
                    val request: Request =
                        chain.request().newBuilder().header(
                            "Authorization",
                            "Bearer ${AuthService.getAccessToken().blockingGet()}"
                        ).build()
                    return chain.proceed(request)
                }
            })
        }

        return httpClientBuilder
    }

    fun getBuilder(withAuthorization: Boolean): Retrofit.Builder = Retrofit.Builder()
        .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
        .addConverterFactory(MoshiConverterFactory.create(Moshi.Builder().build()).asLenient())
        .baseUrl(baseUrl)
        .client(getHttpClientBuilder(withAuthorization).build())

}