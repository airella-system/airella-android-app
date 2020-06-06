package org.airella.airella.utils

import java.util.*

object Config {
    val SERVICE_UUID: UUID = UUID.fromString("f1eb6601-af50-4cf3-9f6e-4e1c6fb8e88c")

    val SSID_CHARACTERISTIC_UUID: UUID = UUID.fromString("45bee5c6-8043-4c6e-b497-fad68a340b70")
    val WIFI_PASSWORD_CHARACTERISTIC_UUID: UUID =
        UUID.fromString("0851dd07-b59f-40c6-8114-357c7dff694c")
    val REFRESH_DEVICE_CHARACTERISTIC_UUID: UUID =
        UUID.fromString("2ca7df70-42df-482e-81cf-468e0fcec5dc")
    val REGISTRATION_TOKEN_UUID: UUID = UUID.fromString("cb520851-1fd3-446e-a590-e777ddd0232c")
    val API_URL_UUID: UUID = UUID.fromString("20418184-e336-4409-a04d-61d7cf31f56b")


}