package org.airella.airella.config

enum class InternetConnectionType(val code: String) {
    NONE("-1"),
    WIFI("0"),
    GSM("1");

    companion object {
        fun getByCode(code: String): InternetConnectionType =
            try {
                values().first { it.code == code }
            } catch (_: Exception) {
                NONE
            }
    }
}