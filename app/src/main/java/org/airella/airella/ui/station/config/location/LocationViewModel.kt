package org.airella.airella.ui.station.config.location

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.airella.airella.data.model.common.Location

class LocationViewModel : ViewModel() {

    val location: MutableLiveData<Location> = MutableLiveData()
}