package org.airella.airella.ui.station.config

import android.bluetooth.BluetoothDevice
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

open class ConfigViewModel : ViewModel() {

    lateinit var btDevice: BluetoothDevice

    val stationName: MutableLiveData<String> = MutableLiveData()

    val stationWifiSSID: MutableLiveData<String> = MutableLiveData()

    val stationCountry: MutableLiveData<String> = MutableLiveData()
    val stationCity: MutableLiveData<String> = MutableLiveData()
    val stationStreet: MutableLiveData<String> = MutableLiveData()
    val stationHouseNo: MutableLiveData<String> = MutableLiveData()

    val stationLatitude: MutableLiveData<String> = MutableLiveData()
    val stationLongitude: MutableLiveData<String> = MutableLiveData()

}