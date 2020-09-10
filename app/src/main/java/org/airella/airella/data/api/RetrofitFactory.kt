package org.airella.airella.data.api

import com.squareup.moshi.Moshi
import hu.akarnokd.rxjava3.retrofit.RxJava3CallAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory


object RetrofitFactory {

    private val retrofit: Retrofit.Builder

    const val baseUrl = "http://airella.cyfrogen.com/api/"

    init {
        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)
        val httpClient = OkHttpClient.Builder()
        httpClient.addInterceptor(logging)

        val moshi = Moshi.Builder().build()

        retrofit = Retrofit.Builder()
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .addConverterFactory(MoshiConverterFactory.create(moshi).asLenient())
            .client(httpClient.build())
    }

    fun getBuilder(): Retrofit.Builder = retrofit

}