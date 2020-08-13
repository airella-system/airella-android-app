package org.airella.airella.ui.station.config

import android.bluetooth.BluetoothDevice
import android.content.Context
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.airella.airella.data.bluetooth.BluetoothCallback
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
        val characteristicWriteQueue = LinkedList(
            listOf(
                Pair(Config.WIFI_SSID_UUID, wifiSSID),
                Pair(Config.WIFI_PASSWORD_UUID, wifiPassword),
                Pair(Config.REFRESH_ACTION_UUID, Config.WIFI_ACTION)
            )
        )
        btDevice.connectGatt(context, false, object : BluetoothCallback(characteristicWriteQueue) {
            override fun onConnected() = setStatus("Connected")
            override fun onFailToConnect() = setStatus("Failed to connect")
            override fun onProgress() = setStatus("Configuring in progress...")
            override fun onSuccess() = setStatus("Success")
            override fun onFailure() = setStatus("Configuration failed")
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
        val characteristicWriteQueue = LinkedList(
            listOf(
                Pair(Config.STATION_COUNTRY_UUID, country),
                Pair(Config.STATION_CITY_UUID, city),
                Pair(Config.STATION_STREET_UUID, street),
                Pair(Config.STATION_HOUSE_NO_UUID, houseNo),
                Pair(Config.REFRESH_ACTION_UUID, Config.ADDRESS_ACTION)
            )
        )
        btDevice.connectGatt(context, false,
            object : BluetoothCallback(characteristicWriteQueue) {
                override fun onConnected() = setStatus("Connected")
                override fun onFailToConnect() = setStatus("Failed to connect")
                override fun onProgress() = setStatus("Saving address in progress...")
                override fun onSuccess() = setStatus("Success")
                override fun onFailure() = setStatus("Saving address failed")
            })
    }

    fun saveLocation(context: Context, latitude: String, longitude: String) {
        Log.i("Save location start")
        val characteristicWriteQueue = LinkedList(
            listOf(
                Pair(Config.LOCATION_LATITUDE_UUID, latitude),
                Pair(Config.LOCATION_LONGITUDE_UUID, longitude),
                Pair(Config.LOCATION_MANUALLY_UUID, "1"),
                Pair(Config.REFRESH_ACTION_UUID, Config.LOCATION_ACTION)
            )
        )
        btDevice.connectGatt(context, false, object : BluetoothCallback(characteristicWriteQueue) {
            override fun onConnected() = setStatus("Connected")
            override fun onFailToConnect() = setStatus("Failed to connect")
            override fun onProgress() = setStatus("Saving location in progress...")
            override fun onSuccess() = setStatus("Success")
            override fun onFailure() = setStatus("Saving location failed")
        })
    }

    fun registerStation(context: Context, stationName: String) {
        Log.i("Register station start")
        val characteristicWriteQueue = LinkedList(
            listOf(
                Pair(Config.STATION_NAME_UUID, stationName),
                Pair(Config.REGISTRATION_TOKEN_UUID, AuthService.user!!.stationRegistrationToken),
                Pair(Config.API_URL_UUID, Config.DEFAULT_API_URL),
                Pair(Config.REFRESH_ACTION_UUID, Config.REGISTER_ACTION)
            )
        )
        btDevice.connectGatt(context, false, object : BluetoothCallback(characteristicWriteQueue) {
            override fun onConnected() = setStatus("Connected")
            override fun onFailToConnect() = setStatus("Failed to connect")
            override fun onProgress() = setStatus("Configuring in progress...")
            override fun onSuccess() = setStatus("Success")
            override fun onFailure() = setStatus("Registration failed")
        })
    }

    fun saveStationPassword(context: Context, newPassword: String) {
        Log.i("Change password start")
        Log.i("New password: $newPassword")
        setStatus("Not implemented")
//        val characteristicWriteQueue = LinkedList<Pair<UUID, String>>().apply {
//            add(Pair(Config.LOCATION_LATITUDE_UUID, newPassword))
//            add(Pair(Config.REFRESH_DEVICE_UUID, "location"))
//        }
//        btDevice.connectGatt(context, false, object : BluetoothCallback() {
//        })
    }

    fun hardResetDevice(context: Context) {
        Log.i("Hard reset started")
        setStatus("Connecting")
        val characteristicWriteQueue = LinkedList(listOf(Pair(Config.CLEAR_DATA_UUID, "")))

        btDevice.connectGatt(context,
            false,
            object : BluetoothCallback(characteristicWriteQueue) {
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


