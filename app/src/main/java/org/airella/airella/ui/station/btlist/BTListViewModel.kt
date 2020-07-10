package org.airella.airella.ui.station.btlist

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.airella.airella.data.service.BluetoothService
import org.airella.airella.utils.Log

class BTListViewModel : ViewModel() {

    val btDevicesList: MutableLiveData<List<BluetoothDevice>> = MutableLiveData()

    private val scanCallback = object : ScanCallback() {
        override fun onScanFailed(errorCode: Int) {
            super.onScanFailed(errorCode)
            Log.e("Scan failed with code $errorCode")
        }

        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            super.onScanResult(callbackType, result)
            val devices = btDevicesList.value?.toMutableList() ?: arrayListOf()
            result?.device?.let { devices.add(it) }
            btDevicesList.value = devices.distinct()
        }

        override fun onBatchScanResults(results: MutableList<ScanResult>?) {
            super.onBatchScanResults(results)
            results?.apply {
                addAllDevices(map { it.device })
            }
        }
    }

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

    fun startBtScan(bluetoothAdapter: BluetoothAdapter) {
        BluetoothService.scanBTDevices(bluetoothAdapter, scanCallback, true)
    }

    fun stopBtScan(bluetoothAdapter: BluetoothAdapter) {
        BluetoothService.scanBTDevices(bluetoothAdapter, scanCallback, false)
    }
}
