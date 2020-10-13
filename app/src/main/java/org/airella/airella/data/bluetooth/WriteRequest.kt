package org.airella.airella.data.bluetooth

import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattService
import org.airella.airella.config.Characteristic
import org.airella.airella.config.Config.BT_MTU
import org.airella.airella.utils.Log
import java.util.*
import kotlin.math.ceil

class WriteRequest(override val characteristic: Characteristic, val value: String) : BluetoothRequest {

    private val chunksToWrite: LinkedList<ByteArray> = LinkedList()

    private fun remainingChunks() = chunksToWrite.size

    init {
        var bytes = value.toByteArray()
        val chunkCount: Int = ceil((bytes.size + 1) / BT_MTU.toDouble()).toInt()
        bytes = byteArrayOf(chunkCount.toByte()) + bytes
        while (bytes.isNotEmpty()) {
            val endIndex = if (bytes.size > BT_MTU) BT_MTU else bytes.size
            val chunk = bytes.copyOfRange(0, endIndex)
            bytes = bytes.copyOfRange(endIndex, bytes.size)
            chunksToWrite.add(chunk)
        }
    }

    override fun execute(gatt: BluetoothGatt, gattService: BluetoothGattService) {
        val characteristic = gattService.getCharacteristic(characteristic.uuid)
        val chunk = chunksToWrite.remove()
        Log.d("Saving \"${String(chunk)}\" to ${this.characteristic}")
        characteristic.value = chunk

        gatt.writeCharacteristic(characteristic)
    }

    override fun isFinished(): Boolean = remainingChunks() == 0

}