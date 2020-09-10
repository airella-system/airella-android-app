package org.airella.airella

import android.app.Application
import android.content.Context
import android.os.Handler
import android.os.Looper


class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        context = applicationContext
    }

    companion object {
        private val mainThreadHandler = Handler(Looper.getMainLooper())

        fun runOnUIThread(runnable: () -> Unit) = mainThreadHandler.post(runnable)

        private lateinit var context: Context

        val appContext: Context?
            get() = context


    }
}