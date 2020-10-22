package org.airella.airella.data.bluetooth

import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattService
import org.airella.airella.config.Characteristic
import org.airella.airella.utils.Log

class ReadRequest(override val characteristic: Characteristic) : BluetoothRequest {

    private var readBuffer: ByteArray = ByteArray(0)

    private var remainingChunks: Int = 0

    override fun execute(gatt: BluetoothGatt, gattService: BluetoothGattService) {
        val characteristic = gattService.getCharacteristic(characteristic.uuid)
        Log.d("Reading from ${this.characteristic}")
        gatt.readCharacteristic(characteristic)
    }

    override fun isFinished(): Boolean = remainingChunks == 0

    fun appendResult(result: ByteArray) {
        var chunk = result.toList()
        if (readBuffer.isEmpty()) {
            if (chunk.isEmpty()) {
                remainingChunks = 0
                return
            }
            remainingChunks = chunk[0].toInt()
            chunk = chunk.drop(1)
        }
        remainingChunks--
        readBuffer += chunk
    }

    fun getValue() = String(readBuffer)

}