package org.airella.airella.config

import java.util.*

enum class Characteristic(uuid: String) {

    WIFI_SSID("45bee5c6-8043-4c6e-b497-fad68a340b70"),
    WIFI_PASSWORD("0851dd07-b59f-40c6-8114-357c7dff694c"),
    REGISTRATION_TOKEN("cb520851-1fd3-446e-a590-e777ddd0232c"),
    API_URL("20418184-e336-4409-a04d-61d7cf31f56b"),

    STATION_NAME("23d334ae-161c-4024-a634-57b781fde853"),

    STATION_COUNTRY("cb5f7871-ad3c-4d60-85c6-ca48b5d70550"),
    STATION_CITY("fe070113-c0c5-4237-b907-fdc7eb6b4cd9"),
    STATION_STREET("171a79f2-3f0b-4a21-9d6c-12dd318d1582"),
    STATION_HOUSE_NO("1ed02261-8571-45d9-9cdf-7092b2a315e8"),

    LOCATION_LATITUDE("394bf3e9-df5d-4765-9a47-e1fd722ae0cb"),
    LOCATION_LONGITUDE("cca719aa-7cf0-45f2-b2b6-dba82e0d62ab"),
    LOCATION_MANUALLY("54ef86d9-e6b5-42ba-a4a2-518f93350eb2"),

    INTERNET_CONNECTION_TYPE("7d9059a5-f426-4f2f-9050-3c88036beb1b"),

    REFRESH_ACTION("2ca7df70-42df-482e-81cf-468e0fcec5dc"),
    CLEAR_DATA("9023e6f3-223d-4c6c-bd39-ebca35d7e8d0");

    val uuid: UUID = UUID.fromString(uuid)

    companion object {
        fun getByUuid(uuid: String): Characteristic =
            values().first { it.uuid.toString() == uuid }
    }
}