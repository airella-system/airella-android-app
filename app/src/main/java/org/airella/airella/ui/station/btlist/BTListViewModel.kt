package org.airella.airella.ui.station.btlist

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import androidx.lifecycle.ViewModel
import org.airella.airella.data.service.BluetoothService
import org.airella.airella.utils.Log

class BTListViewModel : ViewModel() {

    private val btDevicesList: MutableList<BluetoothDevice> = mutableListOf()

    val adapter: BTDeviceAdapter by lazy { BTDeviceAdapter(btDevicesList) }

    private val scanCallback = object : ScanCallback() {
        override fun onScanFailed(errorCode: Int) {
            super.onScanFailed(errorCode)
            Log.e("Scan failed with code $errorCode")
            // TODO message dla usera
        }

        override fun onScanResult(callbackType: Int, result: ScanResult) {
            super.onScanResult(callbackType, result)
            result.device?.let { addDevice(it) }
        }

        override fun onBatchScanResults(results: MutableList<ScanResult>) {
            super.onBatchScanResults(results)
            results.map { it.device }.forEach { addDevice(it) }
        }
    }

    fun addDevice(btDevice: BluetoothDevice) {
        if (!btDevicesList.contains(btDevice)) {
            btDevicesList.add(btDevice)
            adapter.notifyItemChanged(btDevicesList.lastIndex)
        }
    }

    fun startBtScan(bluetoothAdapter: BluetoothAdapter) {
        BluetoothService.scanBTDevices(bluetoothAdapter, scanCallback, true)
    }

    fun stopBtScan(bluetoothAdapter: BluetoothAdapter) {
        BluetoothService.scanBTDevices(bluetoothAdapter, scanCallback, false)
    }
}
