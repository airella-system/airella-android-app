package org.airella.airella.ui.station.btlist

import android.bluetooth.BluetoothDevice
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class BTListViewModel : ViewModel() {

    val btDevicesList: MutableLiveData<List<BluetoothDevice>> = MutableLiveData()


    fun addDevice(btDevice: BluetoothDevice) {
        val devices = btDevicesList.value?.toMutableList() ?: arrayListOf()
        devices.add(btDevice)
        btDevicesList.value = devices.distinct()
    }

    fun addAllDevices(btDevices: Collection<BluetoothDevice>) {
        val devices = btDevicesList.value?.toMutableList() ?: arrayListOf()
        devices.addAll(btDevices)
        btDevicesList.value = devices.distinct()
    }
}
