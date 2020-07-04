package org.airella.airella.ui.station.config

import android.bluetooth.BluetoothDevice
import androidx.lifecycle.ViewModel

class StationConfigViewModel : ViewModel() {

    lateinit var btDevice: BluetoothDevice

    fun isBonded() = btDevice.bondState == BluetoothDevice.BOND_BONDED
}


