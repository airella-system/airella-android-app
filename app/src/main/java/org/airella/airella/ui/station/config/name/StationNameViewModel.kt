package org.airella.airella.ui.station.config.name

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class StationNameViewModel : ViewModel() {

    val stationName: MutableLiveData<String> = MutableLiveData()
}