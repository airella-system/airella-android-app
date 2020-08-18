package org.airella.airella.data.bluetooth

import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattService

interface BluetoothRequest {

    fun execute(gatt: BluetoothGatt, gattService: BluetoothGattService)

    fun isFinished(): Boolean
}