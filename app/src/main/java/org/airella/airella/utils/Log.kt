package org.airella.airella.utils

object Log {
    private const val loggerTag = "airella-debug"

    fun d(text: String) = android.util.Log.w(loggerTag, text)
    fun i(text: String) = android.util.Log.w(loggerTag, text)
    fun w(text: String) = android.util.Log.w(loggerTag, text)
    fun e(text: String) = android.util.Log.e(loggerTag, text)
}