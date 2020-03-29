package org.airella.airella.ui.map

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MapViewModel : ViewModel() {

    val text = MutableLiveData<String>().apply {
        value = "This is map Fragment"
    }

}