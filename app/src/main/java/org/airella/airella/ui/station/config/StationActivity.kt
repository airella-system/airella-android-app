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
import org.airella.airella.ui.OnBackPressed
import org.airella.airella.ui.station.config.main.StationMainFragment
import org.airella.airella.utils.Log
import java.util.*

class StationActivity : AppCompatActivity() {

    private val btBondBroadcastReceiver = object : BroadcastReceiver() {

        override fun onReceive(context: Context?, intent: Intent) {
            when (intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, BluetoothDevice.ERROR)) {
                BluetoothDevice.BOND_NONE -> Log.w("Bonding Failed")
                BluetoothDevice.BOND_BONDING -> Log.d("Bonding...")
                BluetoothDevice.BOND_BONDED -> {
                    unregisterReceiver(this)
                    Log.d("Bonded!")
                    getStationConfig()
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.container, StationMainFragment())
                        .commitNow()
                }
            }
        }
    }

    private val viewModel: ConfigViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_container)

        if (savedInstanceState == null) {
            viewModel.btDevice = intent.extras!!.getParcelable("bt_device")!!

            if (viewModel.btDevice.bondState == BluetoothDevice.BOND_BONDED) {
                getStationConfig()
                supportFragmentManager.beginTransaction()
                    .replace(R.id.container, StationMainFragment())
                    .commitNow()
            } else {
                registerReceiver(
                    btBondBroadcastReceiver,
                    IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED)
                )

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
                ReadRequest(Characteristic.STATION_NAME) {
                    viewModel.stationName.value = it
                },

                ReadRequest(Characteristic.INTERNET_CONNECTION_TYPE) {
                    viewModel.connectionType.value = InternetConnectionType.getByCode(it)
                },
                ReadRequest(Characteristic.WIFI_SSID) {
                    viewModel.stationWifiSSID.value = it
                },
                ReadRequest(Characteristic.GSM_EXTENDER_URL) {
                    viewModel.gsmExtenderUrl.value = it
                },
                ReadRequest(Characteristic.GSM_CONFIG) {
                    viewModel.setApnConfig(it)
                },

                ReadRequest(Characteristic.STATION_COUNTRY) {
                    viewModel.stationCountry.value = it
                },
                ReadRequest(Characteristic.STATION_CITY) {
                    viewModel.stationCity.value = it
                },
                ReadRequest(Characteristic.STATION_STREET) {
                    viewModel.stationStreet.value = it
                },
                ReadRequest(Characteristic.STATION_HOUSE_NO) {
                    viewModel.stationHouseNo.value = it
                },

                ReadRequest(Characteristic.LOCATION_LATITUDE) {
                    viewModel.stationLatitude.value = it
                },
                ReadRequest(Characteristic.LOCATION_LONGITUDE) {
                    viewModel.stationLongitude.value = it
                },

                ReadRequest(Characteristic.DEVICE_STATUS) {
                    viewModel.setStatus(it)
                }
            )
        )
        viewModel.btDevice.connectGatt(this, false, BluetoothCallback(bluetoothRequests))
    }

    override fun onBackPressed() {
        val fragment =
            this.supportFragmentManager.findFragmentById(R.id.container)
        if ((fragment as? OnBackPressed)?.onBackPressed()?.not() ?: true) {
            super.onBackPressed()
        }
    }
}