package org.airella.airella.ui.station.config

import android.bluetooth.BluetoothDevice
import androidx.lifecycle.ViewModel

open class ConfigViewModel : ViewModel() {

    lateinit var btDevice: BluetoothDevice

}