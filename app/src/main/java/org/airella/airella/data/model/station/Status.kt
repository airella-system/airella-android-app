package org.airella.airella.data.model.station

data class Status(val status: Boolean, val code: String) {
    constructor(code: String) : this((arrayOf("OK", "YES").contains(code)), code)
}