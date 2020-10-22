package org.airella.airella.ui.station.config

import android.bluetooth.BluetoothDevice
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.airella.airella.config.InternetConnectionType

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


    private val apnRegex: Regex = """"(.*?)","(.*?)",(.*?)"""".toRegex()

    fun setApnConfig(config: String) {
        apnRegex.find(config)?.let {
            val (apn, username, password) = it.destructured
            gsmApn.value = apn
            gsmUsername.value = username
            gsmPassword.value = password
        }
    }

}