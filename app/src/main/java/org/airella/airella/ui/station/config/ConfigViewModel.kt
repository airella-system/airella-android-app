package org.airella.airella.ui.station.config

import android.bluetooth.BluetoothDevice
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.airella.airella.config.InternetConnectionType
import org.airella.airella.data.model.station.Status
import org.airella.airella.utils.Log

open class ConfigViewModel : ViewModel() {

    lateinit var btDevice: BluetoothDevice

    val stationName: MutableLiveData<String> = MutableLiveData()

    val connectionType: MutableLiveData<InternetConnectionType> = MutableLiveData()

    val stationWifiSSID: MutableLiveData<String> = MutableLiveData()

    val gsmApn: MutableLiveData<String> = MutableLiveData()
    val gsmUsername: MutableLiveData<String> = MutableLiveData()
    val gsmPassword: MutableLiveData<String> = MutableLiveData()
    val gsmExtenderUrl: MutableLiveData<String> = MutableLiveData()

    val stationCountry: MutableLiveData<String> = MutableLiveData()
    val stationCity: MutableLiveData<String> = MutableLiveData()
    val stationStreet: MutableLiveData<String> = MutableLiveData()
    val stationHouseNo: MutableLiveData<String> = MutableLiveData()

    val stationLatitude: MutableLiveData<String> = MutableLiveData()
    val stationLongitude: MutableLiveData<String> = MutableLiveData()

    val registered: MutableLiveData<Status> = MutableLiveData()
    val apiConnection: MutableLiveData<Status> = MutableLiveData()
    val airSensor: MutableLiveData<Status> = MutableLiveData()
    val gps: MutableLiveData<Status> = MutableLiveData()
    val heater: MutableLiveData<Status> = MutableLiveData()
    val powerSensor: MutableLiveData<Status> = MutableLiveData()
    val weatherStatus: MutableLiveData<Status> = MutableLiveData()


    private val apnRegex: Regex = """"(.*?)","(.*?)",(.*?)"""".toRegex()

    fun setApnConfig(config: String) {
        apnRegex.find(config)?.let {
            val (apn, username, password) = it.destructured
            gsmApn.value = apn
            gsmUsername.value = username
            gsmPassword.value = password
        }
    }

    fun setStatus(statusString: String) {
        statusString.split(",").forEach {
            try {
                val (name, status) = it.split("|", limit = 2)
                when (name) {
                    "REGISTERED" -> registered.value = Status(status)
                    "API_CONNECTION" -> apiConnection.value = Status(status)
                    "AIR_SENSOR" -> airSensor.value = Status(status)
                    "GPS" -> gps.value = Status(status)
                    "HEATER" -> heater.value = Status(status)
                    "POWER_SENSOR" -> powerSensor.value = Status(status)
                    "WEATHER_SENSOR" -> weatherStatus.value = Status(status)
                    else -> Log.w("Unexpected status name")
                }
            } catch (_: Throwable) {
            }
        }
    }

}