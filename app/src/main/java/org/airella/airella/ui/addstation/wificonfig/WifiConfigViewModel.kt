package org.airella.airella.ui.addstation.wificonfig

import android.bluetooth.*
import android.content.Context
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.airella.airella.utils.Config
import org.airella.airella.utils.Log


class WifiConfigViewModel : ViewModel() {

    lateinit var btDevice: BluetoothDevice

    private var wifiSSID: String = ""
    private var wifiPassword: String = ""

    val statusToast: MutableLiveData<String> = MutableLiveData()

    fun wifiSSIDChanged(wifiSSID: String) {
        this.wifiSSID = wifiSSID
    }

    fun wifiPasswordChanged(wifiPassword: String) {
        this.wifiPassword = wifiPassword
    }


    fun saveConfig(context: Context) {
        Log.e("Connecting")
        btDevice.connectGatt(context, false, MyBluetoothGattCallback(context))
    }


    inner class MyBluetoothGattCallback(private val context: Context?) : BluetoothGattCallback() {

        private lateinit var gatt: BluetoothGatt

        private val gattService: BluetoothGattService by lazy { gatt.getService(Config.SERVICE_UUID) }

        private val saidCharacteristic: BluetoothGattCharacteristic by lazy {
            (gattService.getCharacteristic(Config.SSID_CHARACTERISTIC_UUID))
        }
        private val passCharacteristic: BluetoothGattCharacteristic by lazy {
            (gattService.getCharacteristic(Config.WIFI_PASWORD_CHARACTERISTIC_UUID))
        }
        private val refreshCharacteristic: BluetoothGattCharacteristic by lazy {
            (gattService.getCharacteristic(Config.REFRESH_DEVICE_CHARACTERISTIC_UUID))
        }

        override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
            super.onConnectionStateChange(gatt, status, newState)
            this.gatt = gatt!!
            if (newState == BluetoothGatt.STATE_CONNECTED) {
                Log.e("Connected")
                gatt.discoverServices()
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
            Log.e("Discovered services")
            saidCharacteristic.setValue(wifiSSID)
            gatt!!.writeCharacteristic(saidCharacteristic)
            super.onServicesDiscovered(gatt, status)
        }

        override fun onCharacteristicWrite(
            gatt: BluetoothGatt?,
            characteristic: BluetoothGattCharacteristic?,
            status: Int
        ) {
            super.onCharacteristicWrite(gatt, characteristic, status)
            Log.e("${characteristic!!.uuid}, $status")
            when (status) {
                BluetoothGatt.GATT_SUCCESS -> {
                    when (characteristic.uuid) {
                        Config.SSID_CHARACTERISTIC_UUID -> {
                            passCharacteristic.setValue(wifiPassword)
                            gatt!!.writeCharacteristic(passCharacteristic)
                        }
                        Config.WIFI_PASWORD_CHARACTERISTIC_UUID -> {
                            refreshCharacteristic.setValue("")
                            gatt!!.writeCharacteristic(refreshCharacteristic)
                        }
                        Config.REFRESH_DEVICE_CHARACTERISTIC_UUID -> {
                            Log.e("SUCCESS CONFIGURATION")
                            Handler(Looper.getMainLooper()).post {
                                statusToast.value = "Configuration successful"
                            }
                        }
                    }

                }
                BluetoothGatt.GATT_FAILURE -> Log.e("FAIL")
                BluetoothGatt.GATT_INSUFFICIENT_AUTHENTICATION -> Log.e("AUTH_REQ")
            }
        }
    }

}
