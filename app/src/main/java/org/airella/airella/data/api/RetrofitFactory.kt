package org.airella.airella.data.api

import com.squareup.moshi.Moshi
import hu.akarnokd.rxjava3.retrofit.RxJava3CallAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object RetrofitFactory {

    private val retrofit: Retrofit.Builder

    const val baseUrl = "http://airella.cyfrogen.com/api/"

    init {
        val moshi = Moshi.Builder().build()

        retrofit = Retrofit.Builder()
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .addConverterFactory(MoshiConverterFactory.create(moshi).asLenient())
    }

    fun getBuilder(): Retrofit.Builder = retrofit

}