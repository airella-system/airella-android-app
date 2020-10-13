package org.airella.airella.config

import java.util.*

object Config {
    const val BT_MTU = 20

    const val BT_ENABLE_CONST = 4
    const val REQUEST_FINE_LOCATION_CONST = 99

    val SERVICE_UUID: UUID = UUID.fromString("f1eb6601-af50-4cf3-9f6e-4e1c6fb8e88c")

    const val DEFAULT_API_URL = "https://airella.cyfrogen.com/api"
}