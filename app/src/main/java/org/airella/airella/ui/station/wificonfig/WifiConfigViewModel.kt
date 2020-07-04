package org.airella.airella.ui.station.wificonfig

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

    private val mtu = 128

    val status: MutableLiveData<String> = MutableLiveData()
    val errorStatus: MutableLiveData<String> = MutableLiveData()

    fun wifiSSIDChanged(wifiSSID: String) {
        this.wifiSSID = wifiSSID
    }

    fun wifiPasswordChanged(wifiPassword: String) {
        this.wifiPassword = wifiPassword
    }


    fun saveConfig(context: Context) {
        setStatus("Connecting")
        btDevice.connectGatt(context, false, MyBluetoothGattCallback())
    }

    private val mainThreadHandler = Handler(Looper.getMainLooper())

    private fun setStatus(status: String) {
        Log.i(status)
        mainThreadHandler.post { this@WifiConfigViewModel.status.value = status }
    }

    private fun setErrorStatus(status: String) {
        Log.w(status)
        mainThreadHandler.post { this@WifiConfigViewModel.errorStatus.value = status }
    }

    inner class MyBluetoothGattCallback : BluetoothGattCallback() {

        private lateinit var gattService: BluetoothGattService

        private val ssidCharacteristic: BluetoothGattCharacteristic by lazy {
            (gattService.getCharacteristic(Config.SSID_CHARACTERISTIC_UUID))
        }
        private val passCharacteristic: BluetoothGattCharacteristic by lazy {
            (gattService.getCharacteristic(Config.WIFI_PASSWORD_CHARACTERISTIC_UUID))
        }
        private val registrationTokenCharacteristic: BluetoothGattCharacteristic by lazy {
            (gattService.getCharacteristic(Config.REGISTRATION_TOKEN_UUID))
        }
        private val apiUrlCharacteristic: BluetoothGattCharacteristic by lazy {
            (gattService.getCharacteristic(Config.API_URL_UUID))
        }
        private val refreshCharacteristic: BluetoothGattCharacteristic by lazy {
            (gattService.getCharacteristic(Config.REFRESH_DEVICE_CHARACTERISTIC_UUID))
        }

        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            super.onConnectionStateChange(gatt, status, newState)
            if (newState == BluetoothGatt.STATE_CONNECTED) {
                setStatus("Connected")
                gatt.requestMtu(mtu)
            } else if (newState != BluetoothGatt.STATE_DISCONNECTED) {
                Log.w("Failed to connect")
                setErrorStatus("Failed to connect")
            }
        }

        override fun onMtuChanged(gatt: BluetoothGatt, mtu: Int, status: Int) {
            Log.i("New MTU: $mtu - status: $status")
            gatt.discoverServices()
            super.onMtuChanged(gatt, mtu, status)
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            Log.i("Discovered services")
            val gattService = gatt.getService(Config.SERVICE_UUID)
            if (gattService == null) {
                setStatus("Configuring failed")
                setErrorStatus("This device isn't an Airella Station")
                Log.e("This device isn't an Airella Station")
                return
            }
            this.gattService = gattService
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
                            setStatus("Configuring successful")
                            gatt.disconnect()
                        }
                    }
                }
                BluetoothGatt.GATT_FAILURE -> Log.w("FAIL")
                BluetoothGatt.GATT_INSUFFICIENT_AUTHENTICATION, BluetoothGatt.GATT_INSUFFICIENT_ENCRYPTION -> Log.w(
                    "AUTH_REQ"
                )
            }
        }
    }
}
