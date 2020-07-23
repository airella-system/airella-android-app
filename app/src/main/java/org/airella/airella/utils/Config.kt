package org.airella.airella.utils

import java.util.*

object Config {
    val SERVICE_UUID: UUID = UUID.fromString("f1eb6601-af50-4cf3-9f6e-4e1c6fb8e88c")

    val WIFI_SSID_UUID = uuid("45bee5c6-8043-4c6e-b497-fad68a340b70")
    val WIFI_PASSWORD_UUID = uuid("0851dd07-b59f-40c6-8114-357c7dff694c")
    val REGISTRATION_TOKEN_UUID = uuid("cb520851-1fd3-446e-a590-e777ddd0232c")
    val API_URL_UUID = uuid("20418184-e336-4409-a04d-61d7cf31f56b")
    val DEVICE_PASSWORD_UUID = uuid("126b5b11-6590-4229-8026-ba30ad032133")

    val STATION_NAME_UUID = uuid("23d334ae-161c-4024-a634-57b781fde853")

    val STATION_COUNTRY_UUID = uuid("cb5f7871-ad3c-4d60-85c6-ca48b5d70550")
    val STATION_CITY_UUID = uuid("fe070113-c0c5-4237-b907-fdc7eb6b4cd9")
    val STATION_STREET_UUID = uuid("171a79f2-3f0b-4a21-9d6c-12dd318d1582")
    val STATION_HOUSE_NO_UUID = uuid("1ed02261-8571-45d9-9cdf-7092b2a315e8")

    val LOCATION_LATITUDE_UUID = uuid("394bf3e9-df5d-4765-9a47-e1fd722ae0cb")
    val LOCATION_LONGITUDE_UUID = uuid("cca719aa-7cf0-45f2-b2b6-dba82e0d62ab")
    val LOCATION_MANUALLY_UUID = uuid("54ef86d9-e6b5-42ba-a4a2-518f93350eb2")

    val REFRESH_ACTION_UUID = uuid("2ca7df70-42df-482e-81cf-468e0fcec5dc")
    val CLEAR_DATA_UUID = uuid("9023e6f3-223d-4c6c-bd39-ebca35d7e8d0")

    const val REGISTER_ACTION = "register"
    const val WIFI_ACTION = "wifi"
    const val ADDRESS_ACTION = "address"
    const val LOCATION_ACTION = "location"
    

    private fun uuid(uuid: String): UUID = UUID.fromString(uuid)
}