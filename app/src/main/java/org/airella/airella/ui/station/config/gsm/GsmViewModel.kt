package org.airella.airella.ui.station.config.gsm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class GsmViewModel : ViewModel() {

    val apn: MutableLiveData<String> = MutableLiveData()
    val username: MutableLiveData<String> = MutableLiveData()
    val password: MutableLiveData<String> = MutableLiveData()
    val extenderUrl: MutableLiveData<String> = MutableLiveData()

}