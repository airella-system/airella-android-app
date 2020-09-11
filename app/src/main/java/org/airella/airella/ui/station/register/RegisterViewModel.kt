package org.airella.airella.ui.station.register

import androidx.fragment.app.Fragment
import org.airella.airella.data.bluetooth.BluetoothRequest
import org.airella.airella.data.bluetooth.WriteRequest
import org.airella.airella.data.service.AuthService
import org.airella.airella.ui.station.AbstractConfigViewModel
import org.airella.airella.utils.Config
import org.airella.airella.utils.Log
import java.util.*

class RegisterViewModel : AbstractConfigViewModel() {

    fun registerStation(fragment: Fragment, stationName: String, apiUrl: String) {
        Log.i("Register station start")
        val bluetoothRequests: Queue<BluetoothRequest> = LinkedList(
            listOf(
                WriteRequest(Config.STATION_NAME_UUID, stationName),
                WriteRequest(
                    Config.REGISTRATION_TOKEN_UUID,
                    AuthService.getUser().stationRegistrationToken
                ),
                WriteRequest(Config.API_URL_UUID, apiUrl),
                WriteRequest(Config.REFRESH_ACTION_UUID, Config.REGISTER_ACTION)
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
