package org.airella.airella.data.service

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import org.airella.airella.MyApplication
import org.airella.airella.data.bluetooth.BluetoothCallback
import org.airella.airella.exception.BluetoothDisabledException
import java.util.*

object BluetoothService {

    private val bluetoothAdapter: BluetoothAdapter by lazy { BluetoothAdapter.getDefaultAdapter() }

    private var isConnected: Boolean = false

    private val tasksQueue: Queue<Pair<BluetoothDevice, BluetoothCallback>> = LinkedList()

    fun connectGatt(btDevice: BluetoothDevice, callback: BluetoothCallback) {
        synchronized(isConnected) {
            if (!isConnected) {
                isConnected = true
                MyApplication.runOnUiThread {
                    btDevice.connectGatt(MyApplication.appContext, false, callback)
                }
            } else {
                tasksQueue.add(Pair(btDevice, callback))
            }
        }
    }

    fun connectFinished() {
        synchronized(isConnected) {
            if (tasksQueue.isNotEmpty()) {
                val request = tasksQueue.remove()
                MyApplication.runOnUiThread {
                    request.first.connectGatt(MyApplication.appContext, false, request.second)
                }
            } else {
                isConnected = false
            }
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