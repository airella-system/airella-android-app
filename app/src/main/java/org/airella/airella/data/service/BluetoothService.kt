package org.airella.airella.data.service

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanSettings
import android.os.ParcelUuid
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import org.airella.airella.config.Config
import org.airella.airella.exception.BluetoothDisabledException
import org.airella.airella.utils.Log
import java.util.*

object BluetoothService {

    private val bluetoothAdapter: BluetoothAdapter by lazy { BluetoothAdapter.getDefaultAdapter() }

    private val scanning: MutableLiveData<Boolean> = MutableLiveData(false)

    private val filters: List<ScanFilter> = listOf(
        ScanFilter.Builder().setServiceUuid(ParcelUuid(Config.SERVICE_UUID)).build()
    )

    private val settings = ScanSettings.Builder()
        .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
        .setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES)
        .setMatchMode(ScanSettings.MATCH_MODE_AGGRESSIVE)
        .setReportDelay(0L)
        .build()

    val isScanning: LiveData<Boolean> = scanning

    @Throws(BluetoothDisabledException::class)
    fun scanBTDevices(callback: ScanCallback, enable: Boolean) {
        try {
            scanning.value = enable
            if (enable) {
                Log.d("Start BT scan")
                bluetoothAdapter.bluetoothLeScanner.startScan(filters, settings, callback)
            } else {
                Log.d("Stop BT scan")
                bluetoothAdapter.bluetoothLeScanner.stopScan(callback)
            }
        } catch (e: NullPointerException) {
            throw BluetoothDisabledException()
        }
    }

    @Throws(BluetoothDisabledException::class)
    fun getDeviceByMac(macAddress: String): BluetoothDevice {
        try {
            return bluetoothAdapter.getRemoteDevice(macAddress.toUpperCase(Locale.getDefault()))
        } catch (e: NullPointerException) {
            throw BluetoothDisabledException()
        }
    }

}