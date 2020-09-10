package org.airella.airella.utils

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers

object RxUtils {

    fun <T> Single<T>.runAsync(): Single<T> =
        this.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

}