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
import org.airella.airella.data.service.AuthService
import org.airella.airella.utils.Config
import org.airella.airella.utils.Log

class StationConfigViewModel : ViewModel() {

    val status: MutableLiveData<String> = MutableLiveData()

    lateinit var btDevice: BluetoothDevice

    fun isBonded() = btDevice.bondState == BluetoothDevice.BOND_BONDED

    fun saveWiFiConfig(context: Context, wifiSSID: String, wifiPassword: String) {
        Log.i("Save wifi config start")
        setStatus("Connecting")
        btDevice.connectGatt(context, false, object : BluetoothCallback() {
            override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
                super.onConnectionStateChange(gatt, status, newState)
                if (newState == BluetoothGatt.STATE_CONNECTED) {
                    setStatus("Connected")
                } else if (newState != BluetoothGatt.STATE_DISCONNECTED) {
                    setStatus("Failed to connect")
                }
            }

            override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
                super.onServicesDiscovered(gatt, status)
                if (gattService == null) {
                    setStatus("Configuring failed")
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
                                refreshDeviceCharacteristic.setValue("")
                                gatt.writeCharacteristic(refreshDeviceCharacteristic)
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

    fun saveStationPassword(context: Context, newPassword: String) {
        Log.i("Change password start")
        Log.i("New password: $newPassword")
        setStatus("Connecting")
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
                setStatus("Changing password...")
                devicePasswordCharacteristic.setValue(newPassword)
                gatt.writeCharacteristic(devicePasswordCharacteristic)
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
                            Config.DEVICE_PASSWORD_UUID -> {
                                setStatus("Saving...")
                                refreshDeviceCharacteristic.setValue("")
                                gatt.writeCharacteristic(refreshDeviceCharacteristic)
                            }
                            Config.REFRESH_DEVICE_UUID -> {
                                setStatus("Change password successful")
                                gatt.disconnect()
                            }
                        }
                    }
                    else -> {
                        setStatus("Change password failed")
                        gatt.disconnect()
                    }
                }
            }
        })
    }

    fun hardResetDevice(context: Context) {
        Log.i("Hard reset started")
        setStatus("Connecting")
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


