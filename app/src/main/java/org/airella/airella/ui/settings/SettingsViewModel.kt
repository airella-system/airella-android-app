package org.airella.airella.ui.settings

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SettingsViewModel : ViewModel() {

    val text = MutableLiveData<String>().apply {
        value = "This is settings Fragment"
    }
}