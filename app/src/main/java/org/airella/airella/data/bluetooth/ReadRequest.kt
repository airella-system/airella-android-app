package org.airella.airella.data.bluetooth

import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattService
import org.airella.airella.utils.Log
import java.util.*

class ReadRequest(val characteristicUUID: UUID) : BluetoothRequest {

    private var readBuffer: ByteArray = ByteArray(0)

    private var remainingChunks: Int = 0

    override fun execute(gatt: BluetoothGatt, gattService: BluetoothGattService) {
        val characteristic = gattService.getCharacteristic(characteristicUUID)
        Log.d("Reading from $characteristicUUID")
        gatt.readCharacteristic(characteristic)
    }

    override fun isFinished(): Boolean = remainingChunks == 0

    fun appendResult(result: ByteArray) {
        var chunk = result.toList()
        if (readBuffer.isEmpty()) {
            remainingChunks = chunk[0].toInt()
            chunk = chunk.drop(1)
        }
        remainingChunks--
        readBuffer += chunk
    }

    fun getValue() = String(readBuffer)

}