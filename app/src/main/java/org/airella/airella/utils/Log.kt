package org.airella.airella.utils

object Log {
    fun v(text: String) = android.util.Log.v("airella", text)
    fun d(text: String) = android.util.Log.d("airella", text)
    fun i(text: String) = android.util.Log.i("airella", text)
    fun w(text: String) = android.util.Log.w("airella", text)
    fun e(text: String) = android.util.Log.e("airella", text)
}