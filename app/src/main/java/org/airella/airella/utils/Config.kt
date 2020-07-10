package org.airella.airella.utils

import java.util.*

object Config {
    val SERVICE_UUID: UUID = UUID.fromString("f1eb6601-af50-4cf3-9f6e-4e1c6fb8e88c")

    val SSID_UUID = uuid("45bee5c6-8043-4c6e-b497-fad68a340b70")
    val WIFI_PASSWORD_UUID = uuid("0851dd07-b59f-40c6-8114-357c7dff694c")
    val REFRESH_DEVICE_UUID = uuid("2ca7df70-42df-482e-81cf-468e0fcec5dc")
    val REGISTRATION_TOKEN_UUID = uuid("cb520851-1fd3-446e-a590-e777ddd0232c")
    val API_URL_UUID = uuid("20418184-e336-4409-a04d-61d7cf31f56b")
    val CLEAR_DATA_CHARACTERISTIC_UUID = uuid("9023e6f3-223d-4c6c-bd39-ebca35d7e8d0")

    private fun uuid(uuid: String): UUID = UUID.fromString(uuid)
}