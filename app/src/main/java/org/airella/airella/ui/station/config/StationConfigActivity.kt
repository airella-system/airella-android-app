package org.airella.airella.ui.station.config;

import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import org.airella.airella.R
import org.airella.airella.config.Characteristic
import org.airella.airella.config.InternetConnectionType
import org.airella.airella.data.bluetooth.BluetoothCallback
import org.airella.airella.data.bluetooth.BluetoothRequest
import org.airella.airella.data.bluetooth.ReadRequest
import org.airella.airella.ui.station.config.list.StationConfigFragment
import org.airella.airella.utils.Log
import java.util.*

class StationConfigActivity : AppCompatActivity() {

    private val btBondBroadcastReceiver = object : BroadcastReceiver() {

        override fun onReceive(context: Context?, intent: Intent) {
            when (intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, BluetoothDevice.ERROR)) {
                BluetoothDevice.BOND_NONE -> Log.w("Bonding Failed")
                BluetoothDevice.BOND_BONDING -> Log.d("Bonding...")
                BluetoothDevice.BOND_BONDED -> {
                    Log.d("Bonded!")
                    getStationConfig()
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.container, StationConfigFragment())
                        .commitNow()
                }
            }
        }
    }

    private val viewModel: ConfigViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_container)

        registerReceiver(
            btBondBroadcastReceiver,
            IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED)
        )

        if (savedInstanceState == null) {
            viewModel.btDevice = intent.extras!!.getParcelable("bt_device")!!

            if (viewModel.btDevice.bondState == BluetoothDevice.BOND_BONDED) {
                getStationConfig()
                supportFragmentManager.beginTransaction()
                    .replace(R.id.container, StationConfigFragment())
                    .commitNow()
            } else {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.container, StationBondFragment())
                    .commitNow()
            }

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            unregisterReceiver(btBondBroadcastReceiver)
        } catch (_: IllegalArgumentException) {
        }
    }

    private fun getStationConfig() {
        val bluetoothRequests: Queue<BluetoothRequest> = LinkedList(
            listOf(
                ReadRequest(Characteristic.STATION_NAME),

                ReadRequest(Characteristic.INTERNET_CONNECTION_TYPE),
                ReadRequest(Characteristic.WIFI_SSID),

//                ReadRequest(Characteristic.GSM_EXTENDER_URL),
//                ReadRequest(Characteristic.GSM_CONFIG),

                ReadRequest(Characteristic.STATION_COUNTRY),
                ReadRequest(Characteristic.STATION_CITY),
                ReadRequest(Characteristic.STATION_STREET),
                ReadRequest(Characteristic.STATION_HOUSE_NO),

                ReadRequest(Characteristic.LOCATION_LATITUDE),
                ReadRequest(Characteristic.LOCATION_LONGITUDE)
            )
        )
        viewModel.btDevice.connectGatt(this, false, object : BluetoothCallback(bluetoothRequests) {

            override fun onCharacteristicRead(characteristic: Characteristic, result: String) {
                runOnUiThread {
                    when (characteristic) {
                        Characteristic.STATION_NAME -> viewModel.stationName.value = result

                        Characteristic.INTERNET_CONNECTION_TYPE ->
                            viewModel.connectionType.value =
                                InternetConnectionType.getByCode(result)
                        Characteristic.WIFI_SSID -> viewModel.stationWifiSSID.value = result

                        Characteristic.GSM_EXTENDER_URL -> viewModel.gsmExtenderUrl.value = result
                        Characteristic.GSM_CONFIG -> viewModel.setApnConfig(result)

                        Characteristic.STATION_COUNTRY -> viewModel.stationCountry.value = result
                        Characteristic.STATION_CITY -> viewModel.stationCity.value = result
                        Characteristic.STATION_STREET -> viewModel.stationStreet.value = result
                        Characteristic.STATION_HOUSE_NO -> viewModel.stationHouseNo.value = result

                        Characteristic.LOCATION_LATITUDE -> viewModel.stationLatitude.value = result
                        Characteristic.LOCATION_LONGITUDE -> viewModel.stationLongitude.value =
                            result
                        else -> {
                        }
                    }
                }
            }
        })
    }
}