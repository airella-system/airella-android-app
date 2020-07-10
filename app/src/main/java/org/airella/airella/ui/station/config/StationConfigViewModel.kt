package org.airella.airella.ui.station.config

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import android.content.Context
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.airella.airella.data.common.BluetoothCallback
import org.airella.airella.utils.Log

class StationConfigViewModel : ViewModel() {

    val status: MutableLiveData<String> = MutableLiveData()

    lateinit var btDevice: BluetoothDevice

    fun isBonded() = btDevice.bondState == BluetoothDevice.BOND_BONDED


    fun hardResetDevice(context: Context) {
        Log.i("Hard reset started")
        btDevice.connectGatt(context, false, object : BluetoothCallback() {
            override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
                super.onConnectionStateChange(gatt, status, newState)
                if (newState != BluetoothGatt.STATE_CONNECTED && newState != BluetoothGatt.STATE_DISCONNECTED) {
                    Log.w("Failed to connect")
                }
            }

            override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
                super.onServicesDiscovered(gatt, status)
                if (gattService == null) {
                    setStatus("Connecting failed")
                    return
                }
                clearDataCharacteristic.setValue("")
                gatt.writeCharacteristic(clearDataCharacteristic)
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
                        setStatus("Hard reset successful")
                        gatt.disconnect()
                    }
                    else -> {
                        setStatus("Hard reset failed")
                        gatt.disconnect()
                    }
                }
            }
        })
    }

    private val mainThreadHandler = Handler(Looper.getMainLooper())

    private fun setStatus(status: String) {
        Log.i(status)
        mainThreadHandler.post { this.status.value = status }
    }

}


