package org.airella.airella.data.bluetooth

import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattService
import org.airella.airella.utils.Config
import org.airella.airella.utils.Log
import java.util.*

const val BT_MTU = 20

open class BluetoothCallback(private val requests: Queue<BluetoothRequest>) :
    BluetoothGattCallback() {

    private lateinit var gattService: BluetoothGattService

    private lateinit var currentRequest: BluetoothRequest

    override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
        super.onConnectionStateChange(gatt, status, newState)
        if (newState == BluetoothGatt.STATE_CONNECTED) {
            gatt.discoverServices()
            onConnected()
        } else if (newState != BluetoothGatt.STATE_DISCONNECTED) {
            onFailToConnect()
            gatt.disconnect()
        }
    }


    override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
        super.onServicesDiscovered(gatt, status)
        Log.i("Discovered services")
        gatt.getService(Config.SERVICE_UUID).let {
            if (it != null) {
                gattService = it
                executeNextRequest(gatt)
            } else {
                onFailure()
            }
        }
    }

    override fun onCharacteristicWrite(
        gatt: BluetoothGatt,
        characteristic: BluetoothGattCharacteristic,
        status: Int
    ) {
        super.onCharacteristicWrite(gatt, characteristic, status)
        Log.i("Saving ${characteristic.uuid} finished with status " + if (status == 0) "Success" else "Failed with code $status")
        when (status) {
            BluetoothGatt.GATT_SUCCESS -> {
                if (currentRequest.isFinished()) {
                    onCharacteristicWrite(characteristic.uuid)
                    executeNextRequest(gatt)
                } else {
                    currentRequest.execute(gatt, gattService)
                }
            }
            else -> {
                gatt.disconnect()
                onFailure()
            }
        }
    }

    override fun onCharacteristicRead(
        gatt: BluetoothGatt,
        characteristic: BluetoothGattCharacteristic,
        status: Int
    ) {
        super.onCharacteristicRead(gatt, characteristic, status)
        when (status) {
            BluetoothGatt.GATT_SUCCESS -> {
                val characteristicValue = characteristic.value
                val readRequest = currentRequest as ReadRequest
                readRequest.appendResult(characteristicValue)
                if (readRequest.isFinished()) {
                    onCharacteristicRead(readRequest.characteristicUUID, readRequest.getValue())
                    executeNextRequest(gatt)
                } else {
                    readRequest.execute(gatt, gattService)
                }
            }
            else -> {
                gatt.disconnect()
                onFailure()
            }
        }
    }

    protected open fun onConnected() {}

    protected open fun onFailToConnect() {}

    protected open fun onCharacteristicWrite(characteristicUUID: UUID) {}

    protected open fun onCharacteristicRead(characteristicUUID: UUID, result: String) {}

    protected open fun onSuccess() {}

    protected open fun onFailure() {}

    private fun executeNextRequest(gatt: BluetoothGatt) {
        if (requests.isNotEmpty()) {
            currentRequest = requests.remove()
            currentRequest.execute(gatt, gattService)
        } else {
            gatt.disconnect()
            onSuccess()
        }
    }


}