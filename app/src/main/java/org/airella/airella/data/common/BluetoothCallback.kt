package org.airella.airella.data.common

import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattService
import org.airella.airella.utils.Config
import org.airella.airella.utils.Log
import java.util.*

open class BluetoothCallback(
    private val characteristicWriteRequests: Queue<Pair<UUID, String>>
) : BluetoothGattCallback() {

    private val mtu = 128

    private var gattService: BluetoothGattService? = null

    override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
        super.onConnectionStateChange(gatt, status, newState)
        if (newState == BluetoothGatt.STATE_CONNECTED) {
            gatt.requestMtu(mtu)
            onConnected()
        } else if (newState != BluetoothGatt.STATE_DISCONNECTED) {
            onFailToConnect()
        }
    }

    override fun onMtuChanged(gatt: BluetoothGatt, mtu: Int, status: Int) {
        super.onMtuChanged(gatt, mtu, status)
        Log.i("New MTU: $mtu - status: $status")
        gatt.discoverServices()
    }

    override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
        super.onServicesDiscovered(gatt, status)
        Log.i("Discovered services")
        gattService = gatt.getService(Config.SERVICE_UUID)
        if (gattService != null) {
            saveNextCharacteristic(gatt)
        } else {
            onFailure()
        }
    }

    override fun onCharacteristicWrite(
        gatt: BluetoothGatt,
        characteristic: BluetoothGattCharacteristic,
        status: Int
    ) {
        super.onCharacteristicWrite(gatt, characteristic, status)
        Log.i("${characteristic.uuid}, $status")
        when (status) {
            BluetoothGatt.GATT_SUCCESS -> {
                saveNextCharacteristic(gatt)
            }
            else -> {
                gatt.disconnect()
                onFailure()
            }
        }
    }

    protected open fun onConnected() {}

    protected open fun onFailToConnect() {}

    protected open fun onProgress() {}

    protected open fun onSuccess() {}

    protected open fun onFailure() {}

    protected open fun saveNextCharacteristic(gatt: BluetoothGatt) {
        if (characteristicWriteRequests.isNotEmpty()) {
            onProgress()
            saveCharacteristic(gatt, characteristicWriteRequests.remove())
        } else {
            gatt.disconnect()
            onSuccess()
        }
    }

    private fun saveCharacteristic(gatt: BluetoothGatt, request: Pair<UUID, String>) {
        saveCharacteristic(gatt, request.first, request.second)
    }

    private fun saveCharacteristic(gatt: BluetoothGatt, characteristicUUID: UUID, value: String) {
        val characteristic = gattService!!.getCharacteristic(characteristicUUID)
        characteristic.setValue(value)
        gatt.writeCharacteristic(characteristic)
    }


}