package org.airella.airella.ui.station.config.wifilist

import android.net.wifi.ScanResult
import androidx.fragment.app.Fragment
import org.airella.airella.data.bluetooth.BluetoothRequest
import org.airella.airella.data.bluetooth.WriteRequest
import org.airella.airella.ui.station.AbstractConfigViewModel
import org.airella.airella.utils.Config
import org.airella.airella.utils.Log
import java.util.*

class WifiListViewModel : AbstractConfigViewModel() {

    val wifiList: MutableList<ScanResult> = mutableListOf()

    fun saveWiFiConfig(fragment: Fragment, wifiSSID: String, wifiPassword: String) {
        Log.i("Save wifi config start")
        val bluetoothRequests: Queue<BluetoothRequest> = LinkedList(
            listOf(
                WriteRequest(Config.WIFI_SSID_UUID, wifiSSID),
                WriteRequest(Config.WIFI_PASSWORD_UUID, wifiPassword),
                WriteRequest(Config.REFRESH_ACTION_UUID, Config.WIFI_ACTION)
            )
        )
        btDevice.connectGatt(
            fragment.requireContext(),
            false,
            object : DefaultConfigBluetoothCallback(bluetoothRequests) {
                override fun onSuccess() {
                    setStatus("Success")
                    if (fragment.isVisible) {
                        fragment.parentFragmentManager.popBackStack()
                    }
                }
            })
    }

}
