package org.airella.airella.config

enum class RefreshAction(val code: String) {
    REGISTER("register"),
    WIFI("wifi"),
    GSM("gsm"),
    ADDRESS("address"),
    LOCATION("location");
}