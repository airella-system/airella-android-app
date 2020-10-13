package org.airella.airella.data.bluetooth

import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattService
import org.airella.airella.config.Characteristic

interface BluetoothRequest {

    val characteristic: Characteristic

    fun execute(gatt: BluetoothGatt, gattService: BluetoothGattService)

    fun isFinished(): Boolean
}