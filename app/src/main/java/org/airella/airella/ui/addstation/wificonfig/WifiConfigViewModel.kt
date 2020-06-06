package org.airella.airella.ui.addstation.wificonfig

import android.bluetooth.*
import android.content.Context
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.airella.airella.data.service.AuthService
import org.airella.airella.utils.Config
import org.airella.airella.utils.Log


class WifiConfigViewModel : ViewModel() {

    lateinit var btDevice: BluetoothDevice

    private var wifiSSID: String = ""
    private var wifiPassword: String = ""

    val status: MutableLiveData<String> = MutableLiveData()
    val errorStatus: MutableLiveData<String> = MutableLiveData()

    fun wifiSSIDChanged(wifiSSID: String) {
        this.wifiSSID = wifiSSID
    }

    fun wifiPasswordChanged(wifiPassword: String) {
        this.wifiPassword = wifiPassword
    }


    fun saveConfig(context: Context) {
        Log.i("Connecting")
        status.value = "Connecting"
        btDevice.connectGatt(context, false, MyBluetoothGattCallback())
    }


    inner class MyBluetoothGattCallback : BluetoothGattCallback() {

        private var gattService: BluetoothGattService? = null

        private val ssidCharacteristic: BluetoothGattCharacteristic by lazy {
            (gattService!!.getCharacteristic(Config.SSID_CHARACTERISTIC_UUID))
        }
        private val passCharacteristic: BluetoothGattCharacteristic by lazy {
            (gattService!!.getCharacteristic(Config.WIFI_PASSWORD_CHARACTERISTIC_UUID))
        }
        private val registrationTokenCharacteristic: BluetoothGattCharacteristic by lazy {
            (gattService!!.getCharacteristic(Config.REGISTRATION_TOKEN_UUID))
        }
        private val apiUrlCharacteristic: BluetoothGattCharacteristic by lazy {
            (gattService!!.getCharacteristic(Config.API_URL_UUID))
        }
        private val refreshCharacteristic: BluetoothGattCharacteristic by lazy {
            (gattService!!.getCharacteristic(Config.REFRESH_DEVICE_CHARACTERISTIC_UUID))
        }

        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            super.onConnectionStateChange(gatt, status, newState)
            if (newState == BluetoothGatt.STATE_CONNECTED) {
                Log.i("Connected")
                setStatus("Connected")
                gatt.discoverServices()
            } else {
                Log.w("Failed to connect")
                setErrorStatus("Failed to connect")
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            Log.i("Discovered services")
            gattService = gatt.getService(Config.SERVICE_UUID)
            if (gattService == null) {
                setStatus("Configuring failed")
                setErrorStatus("This device isn't an Airella Station")
                Log.e("This device isn't an Airella Station")
                return
            }
            ssidCharacteristic.setValue(wifiSSID)
            gatt.writeCharacteristic(ssidCharacteristic)
            super.onServicesDiscovered(gatt, status)
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
                    when (characteristic.uuid) {
                        Config.SSID_CHARACTERISTIC_UUID -> {
                            passCharacteristic.setValue(wifiPassword)
                            gatt.writeCharacteristic(passCharacteristic)
                            setStatus("Configuring in progress: 20%")
                        }
                        Config.WIFI_PASSWORD_CHARACTERISTIC_UUID -> {
                            registrationTokenCharacteristic.setValue(AuthService.user!!.stationRegistrationToken)
                            gatt.writeCharacteristic(registrationTokenCharacteristic)
                            setStatus("Configuring in progress: 40%")
                        }
                        Config.REGISTRATION_TOKEN_UUID -> {
                            apiUrlCharacteristic.setValue("http://airella.cyfrogen.com/api")
                            gatt.writeCharacteristic(apiUrlCharacteristic)
                            setStatus("Configuring in progress: 60%")
                        }
                        Config.API_URL_UUID -> {
                            refreshCharacteristic.setValue("")
                            gatt.writeCharacteristic(refreshCharacteristic)
                            setStatus("Configuring in progress: 80%")
                        }
                        Config.REFRESH_DEVICE_CHARACTERISTIC_UUID -> {
                            Log.i("SUCCESS CONFIGURATION")
                            setStatus("Configuring successful")
                            gatt.disconnect()
                        }
                    }
                }
                BluetoothGatt.GATT_FAILURE -> Log.w("FAIL")
                BluetoothGatt.GATT_INSUFFICIENT_AUTHENTICATION -> Log.w("AUTH_REQ")
            }
        }

        private val mainThreadHandler = Handler(Looper.getMainLooper())

        private fun setStatus(status: String) {
            mainThreadHandler.post { this@WifiConfigViewModel.status.value = status }
        }

        private fun setErrorStatus(status: String) {
            mainThreadHandler.post { this@WifiConfigViewModel.errorStatus.value = status }
        }
    }
}
