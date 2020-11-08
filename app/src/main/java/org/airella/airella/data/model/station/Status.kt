package org.airella.airella.data.model.station

data class Status(val code: String) {

    private val okStatuses = arrayOf("OK", "YES")

    fun isOK(): Boolean = okStatuses.contains(code)
}