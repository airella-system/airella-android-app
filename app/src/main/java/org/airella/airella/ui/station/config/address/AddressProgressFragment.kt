package org.airella.airella.ui.station.config.address

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
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

class AddressProgressFragment : Fragment() {

    private val viewModel: ConfigViewModel by activityViewModels()

    private val addressViewModel: AddressViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_configuration_progress, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val address = addressViewModel.address.value!!
        saveAddress(address.country, address.city, address.street, address.number)
    }

    private fun saveAddress(
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
        viewModel.btDevice.connectGatt(
            context,
            false,
            object : BluetoothCallback(bluetoothRequests) {
                override fun onSuccess() {
                    setStatus("Success")
                    switchFragmentWithBackStack(R.id.container, ConfigurationSuccessfulFragment())
                }
            })
    }

}
