package org.airella.airella

import android.app.Application
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import org.airella.airella.data.service.PreferencesService
import org.airella.airella.utils.Log


class MyApplication : Application() {


    override fun onCreate() {
        super.onCreate()
        context = applicationContext
        PreferencesService.init(applicationContext)


    }

    companion object {
        private val mainThreadHandler = Handler(Looper.getMainLooper())

        fun runOnUIThread(runnable: () -> Unit) = mainThreadHandler.post(runnable)

        private lateinit var context: Context

        val appContext: Context?
            get() = context

//        private val toast by lazy { Toast.makeText(appContext, "", Toast.LENGTH_LONG) }

        fun setStatus(status: String) {
            Log.d(status)
//            runOnUIThread {
//                toast.setText(status)
//                toast.show()
//            }
        }
    }
}