package org.airella.airella.ui.station.config.wifi

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import org.airella.airella.MyApplication.Companion.runOnUIThread
import org.airella.airella.MyApplication.Companion.setStatus
import org.airella.airella.R
import org.airella.airella.data.bluetooth.BluetoothCallback
import org.airella.airella.data.bluetooth.BluetoothRequest
import org.airella.airella.data.bluetooth.WriteRequest
import org.airella.airella.ui.station.config.ConfigViewModel
import org.airella.airella.ui.station.config.success.ConfigurationSuccessfulFragment
import org.airella.airella.utils.Config
import org.airella.airella.utils.FragmentUtils.switchFragmentWithBackStack
import org.airella.airella.utils.Log
import java.util.*

class WifiProgressFragment : Fragment() {

    private val viewModel: ConfigViewModel by activityViewModels()

    private val wifiViewModel: WifiViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_configuration_progress, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        saveWiFiConfig(wifiViewModel.wifiSSID.value!!, wifiViewModel.wifiPassword.value!!)
    }


    private fun saveWiFiConfig(wifiSSID: String, wifiPassword: String) {
        Log.i("Save wifi config start")
        val bluetoothRequests: Queue<BluetoothRequest> = LinkedList(
            listOf(
                WriteRequest(Config.WIFI_SSID_UUID, wifiSSID),
                WriteRequest(Config.WIFI_PASSWORD_UUID, wifiPassword),
                WriteRequest(Config.REFRESH_ACTION_UUID, Config.WIFI_ACTION)
            )
        )
        viewModel.btDevice.connectGatt(
            context,
            false,
            object : BluetoothCallback(bluetoothRequests) {
                override fun onSuccess() {
                    runOnUIThread {
                        setStatus("Success")
                        viewModel.stationWifiSSID.value = wifiSSID
                        switchFragmentWithBackStack(
                            R.id.container,
                            ConfigurationSuccessfulFragment()
                        )
                    }
                }
            })
    }

}
