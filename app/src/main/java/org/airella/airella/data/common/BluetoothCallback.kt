package org.airella.airella.data.common

import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattService
import org.airella.airella.utils.Config
import org.airella.airella.utils.Log
import java.util.*

open class BluetoothCallback : BluetoothGattCallback() {

    private val mtu = 128

    protected var gattService: BluetoothGattService? = null

    protected val ssidCharacteristic by lazy { getCharacteristic(Config.SSID_UUID) }
    protected val passCharacteristic by lazy { getCharacteristic(Config.WIFI_PASSWORD_UUID) }
    protected val registrationTokenCharacteristic by lazy { getCharacteristic(Config.REGISTRATION_TOKEN_UUID) }
    protected val apiUrlCharacteristic by lazy { getCharacteristic(Config.API_URL_UUID) }
    protected val refreshDeviceCharacteristic by lazy { getCharacteristic(Config.REFRESH_DEVICE_UUID) }
    protected val clearDataCharacteristic by lazy { getCharacteristic(Config.CLEAR_DATA_UUID) }
    protected val devicePasswordCharacteristic by lazy { getCharacteristic(Config.DEVICE_PASSWORD_UUID) }

    override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
        super.onConnectionStateChange(gatt, status, newState)
        if (newState == BluetoothGatt.STATE_CONNECTED) {
            gatt.requestMtu(mtu)
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
    }

    private fun getCharacteristic(uuid: UUID): BluetoothGattCharacteristic =
        gattService!!.getCharacteristic(uuid)


}