package org.airella.airella.ui.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.airella.airella.data.model.station.Station

class HomeViewModel : ViewModel() {

    val stationsList: MutableLiveData<List<Station>> = MutableLiveData()

}