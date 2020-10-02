package org.airella.airella.ui.station.config.address

import androidx.fragment.app.Fragment
import org.airella.airella.data.bluetooth.BluetoothRequest
import org.airella.airella.data.bluetooth.WriteRequest
import org.airella.airella.ui.station.AbstractConfigViewModel
import org.airella.airella.utils.Config
import org.airella.airella.utils.Log
import java.util.*

class AddressViewModel : AbstractConfigViewModel() {

    fun saveAddress(
        fragment: Fragment,
        country: String,
        city: String,
        street: String,
        houseNo: String
    ) {
        Log.i("Save address start")
        val bluetoothRequests: Queue<BluetoothRequest> = LinkedList(
            listOf(
                WriteRequest(Config.STATION_COUNTRY_UUID, country),
                WriteRequest(Config.STATION_CITY_UUID, city),
                WriteRequest(Config.STATION_STREET_UUID, street),
                WriteRequest(Config.STATION_HOUSE_NO_UUID, houseNo),
                WriteRequest(Config.REFRESH_ACTION_UUID, Config.ADDRESS_ACTION)
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
