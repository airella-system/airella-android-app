package org.airella.airella.data.bluetooth

import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattService
import org.airella.airella.utils.Config
import org.airella.airella.utils.Log
import java.util.*
import kotlin.math.ceil

open class BluetoothCallback(requests: Queue<Pair<UUID, String>>) : BluetoothGattCallback() {

    private val mtu = 20

    private var gattService: BluetoothGattService? = null

    private val characteristicWriteRequests: Queue<Pair<UUID, ByteArray>>

    init {
        characteristicWriteRequests = LinkedList()
        requests.forEach {
            var bytes = it.second.toByteArray()
            val chunkCount: Int = ceil((bytes.size + 1) / mtu.toDouble()).toInt()
            bytes = byteArrayOf(chunkCount.toByte()) + bytes
            while (bytes.isNotEmpty()) {
                val endIndex = if (bytes.size > mtu) mtu else bytes.size
                val chunk = bytes.copyOfRange(0, endIndex)
                bytes = bytes.copyOfRange(endIndex, bytes.size)
                characteristicWriteRequests.add(Pair(it.first, chunk))
            }
        }
    }

    override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
        super.onConnectionStateChange(gatt, status, newState)
        if (newState == BluetoothGatt.STATE_CONNECTED) {
//            gatt.requestMtu(mtu)
            gatt.discoverServices()
            onConnected()
        } else if (newState != BluetoothGatt.STATE_DISCONNECTED) {
            onFailToConnect()
            gatt.disconnect()
        }
    }

//    override fun onMtuChanged(gatt: BluetoothGatt, mtu: Int, status: Int) {
//        super.onMtuChanged(gatt, mtu, status)
//        when (status) {
//            BluetoothGatt.GATT_SUCCESS -> {
//                Log.i("New MTU: $mtu - status: $status")
//                gatt.discoverServices()
//            }
//            else -> {
//                onFailure()
//                gatt.disconnect()
//            }
//        }
//    }

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
        Log.i("Saving ${characteristic.uuid} finished with status " + if (status == 0) "Success" else "Failed with code $status")
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

    private fun saveCharacteristic(gatt: BluetoothGatt, request: Pair<UUID, ByteArray>) {
        saveCharacteristic(gatt, request.first, request.second)
    }

    private fun saveCharacteristic(
        gatt: BluetoothGatt,
        characteristicUUID: UUID,
        value: ByteArray
    ) {
        val characteristic = gattService!!.getCharacteristic(characteristicUUID)
        Log.d("Saving ${String(value)} to $characteristicUUID")
        characteristic.value = value
        gatt.writeCharacteristic(characteristic)
    }


}