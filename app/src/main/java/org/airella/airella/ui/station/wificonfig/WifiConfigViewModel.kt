package org.airella.airella.ui.station.wificonfig

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import android.content.Context
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.airella.airella.data.common.BluetoothCallback
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
        setStatus("Connecting")
        btDevice.connectGatt(context, false, object : BluetoothCallback() {
            override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
                super.onConnectionStateChange(gatt, status, newState)
                if (newState == BluetoothGatt.STATE_CONNECTED) {
                    setStatus("Connected")
                } else if (newState != BluetoothGatt.STATE_DISCONNECTED) {
                    setErrorStatus("Failed to connect")
                }
            }

            override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
                super.onServicesDiscovered(gatt, status)
                if (gattService == null) {
                    setStatus("Configuring failed")
                    setErrorStatus("This device isn't an Airella Station")
                    return
                }
                ssidCharacteristic.setValue(wifiSSID)
                gatt.writeCharacteristic(ssidCharacteristic)
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
                            Config.SSID_UUID -> {
                                passCharacteristic.setValue(wifiPassword)
                                gatt.writeCharacteristic(passCharacteristic)
                                setStatus("Configuring in progress: 20%")
                            }
                            Config.WIFI_PASSWORD_UUID -> {
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
                            Config.REFRESH_DEVICE_UUID -> {
                                setStatus("Configuring successful")
                                gatt.disconnect()
                            }
                        }
                    }
                    else -> {
                        setStatus("Configuring failed")
                        gatt.disconnect()
                    }
                }
            }
        })
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

}
