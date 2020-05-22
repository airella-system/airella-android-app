package org.airella.airella.data.service

import android.content.Context
import android.content.SharedPreferences
import org.airella.airella.R

object PreferencesService {

    private lateinit var context: Context
    private lateinit var sharedPref: SharedPreferences

    fun putString(key: String, value: String) {
        with(sharedPref.edit()) {
            putString(key, value)
            commit()
        }
    }

    fun getString(key: String, default: String): String =
        sharedPref.getString(key, default) ?: default

    fun remove(key: String) {
        sharedPref.edit().remove(key).apply()
    }

    fun clear() {
        sharedPref.edit().clear().apply()
    }

    fun init(context: Context) {
        this.context = context
        sharedPref = PreferencesService.context.getSharedPreferences(
            PreferencesService.context.getString(R.string.app_name),
            Context.MODE_PRIVATE
        )
    }

}