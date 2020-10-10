package org.airella.airella.ui.station.config;

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import org.airella.airella.R
import org.airella.airella.data.bluetooth.BluetoothCallback
import org.airella.airella.data.bluetooth.BluetoothRequest
import org.airella.airella.data.bluetooth.ReadRequest
import org.airella.airella.ui.station.config.list.StationConfigFragment
import org.airella.airella.utils.Config
import java.util.*

class StationConfigActivity : AppCompatActivity() {

    private val viewModel: ConfigViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_container)

        viewModel.btDevice = intent.extras!!.getParcelable("bt_device")!!

        getStatistics()

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, StationConfigFragment())
                .commitNow()
        }
    }

    private fun getStatistics() {
        val bluetoothRequests: Queue<BluetoothRequest> = LinkedList(
            listOf(
                ReadRequest(Config.STATION_NAME_UUID),

                ReadRequest(Config.WIFI_SSID_UUID),

                ReadRequest(Config.STATION_COUNTRY_UUID),
                ReadRequest(Config.STATION_CITY_UUID),
                ReadRequest(Config.STATION_STREET_UUID),
                ReadRequest(Config.STATION_HOUSE_NO_UUID),

                ReadRequest(Config.LOCATION_LATITUDE_UUID),
                ReadRequest(Config.LOCATION_LONGITUDE_UUID)
            )
        )
        viewModel.btDevice.connectGatt(this, false, object : BluetoothCallback(bluetoothRequests) {

            override fun onCharacteristicRead(characteristicUUID: UUID, result: String) {
                runOnUiThread {
                    when (characteristicUUID) {
                        Config.STATION_NAME_UUID -> viewModel.stationName.value = result

                        Config.WIFI_SSID_UUID -> viewModel.stationWifiSSID.value = result

                        Config.STATION_COUNTRY_UUID -> viewModel.stationCountry.value = result
                        Config.STATION_CITY_UUID -> viewModel.stationCity.value = result
                        Config.STATION_STREET_UUID -> viewModel.stationStreet.value = result
                        Config.STATION_HOUSE_NO_UUID -> viewModel.stationHouseNo.value = result

                        Config.LOCATION_LATITUDE_UUID -> viewModel.stationLatitude.value = result
                        Config.LOCATION_LONGITUDE_UUID -> viewModel.stationLongitude.value = result
                    }
                }
            }
        })
    }
}