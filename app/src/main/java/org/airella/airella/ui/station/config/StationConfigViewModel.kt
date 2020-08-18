package org.airella.airella.ui.station.config

import android.bluetooth.BluetoothDevice
import android.content.Context
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.airella.airella.data.bluetooth.BluetoothCallback
import org.airella.airella.data.bluetooth.BluetoothRequest
import org.airella.airella.data.bluetooth.WriteRequest
import org.airella.airella.data.service.AuthService
import org.airella.airella.utils.Config
import org.airella.airella.utils.Log
import java.util.*

class StationConfigViewModel : ViewModel() {

    val status: MutableLiveData<String> = MutableLiveData()

    lateinit var btDevice: BluetoothDevice

    fun isBonded() = btDevice.bondState == BluetoothDevice.BOND_BONDED

    fun saveWiFiConfig(context: Context, wifiSSID: String, wifiPassword: String) {
        Log.i("Save wifi config start")
        val bluetoothRequests: Queue<BluetoothRequest> = LinkedList(
            listOf(
                WriteRequest(Config.WIFI_SSID_UUID, wifiSSID),
                WriteRequest(Config.WIFI_PASSWORD_UUID, wifiPassword),
                WriteRequest(Config.REFRESH_ACTION_UUID, Config.WIFI_ACTION)
            )
        )
        btDevice.connectGatt(context, false, object : BluetoothCallback(bluetoothRequests) {
            override fun onConnected() = setStatus("Connected")
            override fun onFailToConnect() = setStatus("Failed to connect")
            override fun onSuccess() = setStatus("Success")
            override fun onFailure() = setStatus("Configuration failed")
            override fun onCharacteristicWrite(characteristicUUID: UUID) {
                setStatus("Configuring in progress...")
            }
        })
    }

    fun saveAddress(
        context: Context,
        country: String,
        city: String,
        street: String,
        houseNo: String
    ) {
        Log.i("Save address start")
        val bluetoothRequests: Queue<BluetoothRequest> = LinkedList(
            listOf(
                WriteRequest(Config.STATION_COUNTRY_UUID, country),
                WriteRequest(Config.STATION_CITY_UUID, city),
                WriteRequest(Config.STATION_STREET_UUID, street),
                WriteRequest(Config.STATION_HOUSE_NO_UUID, houseNo),
                WriteRequest(Config.REFRESH_ACTION_UUID, Config.ADDRESS_ACTION)
            )
        )
        btDevice.connectGatt(context, false, object : BluetoothCallback(bluetoothRequests) {
            override fun onConnected() = setStatus("Connected")
            override fun onFailToConnect() = setStatus("Failed to connect")
            override fun onSuccess() = setStatus("Success")
            override fun onFailure() = setStatus("Saving address failed")
            override fun onCharacteristicWrite(characteristicUUID: UUID) {
                setStatus("Saving address in progress...")
            }
        })
    }

    fun saveLocation(context: Context, latitude: String, longitude: String) {
        Log.i("Save location start")
        val bluetoothRequests: Queue<BluetoothRequest> = LinkedList(
            listOf(
                WriteRequest(Config.LOCATION_LATITUDE_UUID, latitude),
                WriteRequest(Config.LOCATION_LONGITUDE_UUID, longitude),
                WriteRequest(Config.LOCATION_MANUALLY_UUID, "1"),
                WriteRequest(Config.REFRESH_ACTION_UUID, Config.LOCATION_ACTION)
            )
        )
        btDevice.connectGatt(context, false, object : BluetoothCallback(bluetoothRequests) {
            override fun onConnected() = setStatus("Connected")
            override fun onFailToConnect() = setStatus("Failed to connect")
            override fun onSuccess() = setStatus("Success")
            override fun onFailure() = setStatus("Saving location failed")
            override fun onCharacteristicWrite(characteristicUUID: UUID) {
                setStatus("Saving location in progress...")
            }
        })
    }

    fun registerStation(context: Context, stationName: String, apiUrl: String) {
        Log.i("Register station start")
        val bluetoothRequests: Queue<BluetoothRequest> = LinkedList(
            listOf(
                WriteRequest(Config.STATION_NAME_UUID, stationName),
                WriteRequest(
                    Config.REGISTRATION_TOKEN_UUID,
                    AuthService.user!!.stationRegistrationToken
                ),
                WriteRequest(Config.API_URL_UUID, apiUrl),
                WriteRequest(Config.REFRESH_ACTION_UUID, Config.REGISTER_ACTION)
            )
        )
        btDevice.connectGatt(context, false, object : BluetoothCallback(bluetoothRequests) {
            override fun onConnected() = setStatus("Connected")
            override fun onFailToConnect() = setStatus("Failed to connect")
            override fun onSuccess() = setStatus("Success")
            override fun onFailure() = setStatus("Registration failed")
            override fun onCharacteristicWrite(characteristicUUID: UUID) {
                setStatus("Configuring in progress...")
            }
        })
    }

//    fun saveStationPassword(context: Context, newPassword: String) {
//        Log.i("Change password start")
//        Log.i("New password: $newPassword")
//        setStatus("Not implemented")
////        val characteristicWriteQueue = LinkedList<Pair<UUID, String>>().apply {
////            add(Pair(Config.LOCATION_LATITUDE_UUID, newPassword))
////            add(Pair(Config.REFRESH_DEVICE_UUID, "location"))
////        }
////        btDevice.connectGatt(context, false, object : BluetoothCallback() {
////        })
//    }

    fun hardResetDevice(context: Context) {
        Log.i("Hard reset started")
        setStatus("Connecting")
        val bluetoothRequests: Queue<BluetoothRequest> = LinkedList(
            listOf(
                WriteRequest(Config.CLEAR_DATA_UUID, "")
            )
        )

        btDevice.connectGatt(context, false, object : BluetoothCallback(bluetoothRequests) {
            override fun onFailToConnect() = setStatus("Failed to connect")
            override fun onSuccess() = setStatus("Hard reset successful")
            override fun onFailure() = setStatus("Hard reset failed")
        })
    }

    private val mainThreadHandler = Handler(Looper.getMainLooper())

    private fun setStatus(status: String) {
        Log.i(status)
        mainThreadHandler.post { this.status.value = status }
    }

}


