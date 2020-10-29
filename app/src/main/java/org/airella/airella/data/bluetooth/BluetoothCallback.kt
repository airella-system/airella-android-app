package org.airella.airella.data.bluetooth

import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattService
import org.airella.airella.MyApplication
import org.airella.airella.MyApplication.Companion.runOnUiThread
import org.airella.airella.config.Config
import org.airella.airella.utils.Log
import java.util.*

open class BluetoothCallback(private val requests: Queue<BluetoothRequest>) :
    BluetoothGattCallback() {

    private lateinit var gattService: BluetoothGattService

    private lateinit var currentRequest: BluetoothRequest

    override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
        super.onConnectionStateChange(gatt, status, newState)
        if (status == BluetoothGatt.GATT_SUCCESS) {
            if (newState == BluetoothGatt.STATE_CONNECTED) {
                runOnUiThread {
                    gatt.discoverServices()
                    onConnected()
                }
            } else if (newState == BluetoothGatt.STATE_DISCONNECTED) {
                gatt.close()
            }
        } else {
            runOnUiThread {
                onFailToConnect()
            }
            gatt.close()
        }
    }


    override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
        super.onServicesDiscovered(gatt, status)
        Log.d("Discovered services")
        gatt.getService(Config.SERVICE_UUID).let {
            if (it != null) {
                gattService = it
                executeNextRequest(gatt)
            } else {
                Log.w("Service is null")
                runOnUiThread {
                    onFailure()
                }
            }
        }
    }

    override fun onCharacteristicWrite(
        gatt: BluetoothGatt,
        characteristic: BluetoothGattCharacteristic,
        status: Int
    ) {
        super.onCharacteristicWrite(gatt, characteristic, status)
        Log.d("Saving to ${currentRequest.characteristic} finished with status " + if (status == 0) "Success" else "Failed with code $status")
        when (status) {
            BluetoothGatt.GATT_SUCCESS -> {
                val writeRequest = currentRequest as WriteRequest
                if (writeRequest.isFinished()) {
                    Log.i("Saved \"${(writeRequest).value}\" to ${writeRequest.characteristic} successfully")
                    runOnUiThread {
                        writeRequest.onSuccess()
                    }
                    executeNextRequest(gatt)
                } else {
                    writeRequest.execute(gatt, gattService)
                }
            }
            else -> {
                gatt.close()
                Log.w("Saving characteristic failed, status: $status")
                runOnUiThread {
                    onFailure()
                }
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
                Log.d("Read from ${readRequest.characteristic} value \"${String(characteristicValue)}\"")
                readRequest.appendResult(characteristicValue)
                if (readRequest.isFinished()) {
                    val result = readRequest.getValue()
                    Log.i("Read \"$result\" from ${readRequest.characteristic} successfully")
                    runOnUiThread {
                        readRequest.onSuccess()
                    }
                    executeNextRequest(gatt)
                } else {
                    readRequest.execute(gatt, gattService)
                }
            }
            else -> {
                gatt.close()
                Log.w("Reading characteristic failed, status: $status")
                runOnUiThread {
                    onFailure()
                }
            }
        }
    }

    protected open fun onConnected() = MyApplication.setStatus("Connected")

    protected open fun onFailToConnect() = MyApplication.setStatus("Failed to connect")

    protected open fun onFailure() = MyApplication.setStatus("Failed")

    protected open fun onSuccess() = MyApplication.setStatus("Success")


    private fun executeNextRequest(gatt: BluetoothGatt) {
        if (requests.isNotEmpty()) {
            currentRequest = requests.remove()
            currentRequest.execute(gatt, gattService)
        } else {
            gatt.close()
            runOnUiThread {
                onSuccess()
            }
        }
    }


}