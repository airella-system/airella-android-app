package org.airella.airella.ui.station.config.wifi

import android.net.wifi.ScanResult
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class WifiViewModel : ViewModel() {

    val wifiList: MutableList<ScanResult> = mutableListOf()

    var wifiSSID: MutableLiveData<String> = MutableLiveData()
    var wifiPassword: MutableLiveData<String> = MutableLiveData()

}
