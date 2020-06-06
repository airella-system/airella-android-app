package org.airella.airella.data.service

import android.bluetooth.BluetoothAdapter
import android.bluetooth.le.ScanCallback
import android.os.Handler
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import org.airella.airella.utils.Log

object BluetoothService {

    private const val scanPeriod = 10000L

    private val scanning: MutableLiveData<Boolean> = MutableLiveData(false)

    val isScanning: LiveData<Boolean> = scanning

    fun scanBTDevices(bluetoothAdapter: BluetoothAdapter, callback: ScanCallback, enable: Boolean) {
        when (enable) {
            true -> {
                if (!scanning.value!!) {
                    Handler().postDelayed({
                        if (scanning.value!!) {
                            bluetoothAdapter.bluetoothLeScanner.stopScan(callback)
                            scanning.value = false
                            Log.e("Stop scan")
                        }
                    }, scanPeriod)
                    scanning.value = true
                    Log.e("Start scan")
                    bluetoothAdapter.bluetoothLeScanner.startScan(callback)
                }
            }
            else -> {
                if (scanning.value!!) {
                    bluetoothAdapter.bluetoothLeScanner.stopScan(callback)
                    scanning.value = false
                    Log.e("Stop scan")
                }
            }
        }
    }
}