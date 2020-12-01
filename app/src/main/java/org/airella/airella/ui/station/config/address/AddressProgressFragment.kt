package org.airella.airella.ui.station.config.address

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import org.airella.airella.R
import org.airella.airella.config.Characteristic
import org.airella.airella.config.RefreshAction
import org.airella.airella.data.bluetooth.BluetoothCallback
import org.airella.airella.data.bluetooth.BluetoothRequest
import org.airella.airella.data.bluetooth.WriteRequest
import org.airella.airella.data.service.BluetoothService
import org.airella.airella.ui.station.config.ConfigViewModel
import org.airella.airella.ui.station.config.fail.ConfigurationFailedFragment
import org.airella.airella.ui.station.config.success.ConfigurationSuccessfulFragment
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
        val bluetoothRequests: Queue<BluetoothRequest> = LinkedList<BluetoothRequest>(
            listOf(
                WriteRequest(Characteristic.STATION_COUNTRY, country),
                WriteRequest(Characteristic.STATION_CITY, city),
                WriteRequest(Characteristic.STATION_STREET, street),
                WriteRequest(Characteristic.STATION_HOUSE_NO, houseNo),
                WriteRequest(Characteristic.REFRESH_ACTION, RefreshAction.ADDRESS.code)
            )
        ).apply {
            addAll(viewModel.getStatusReadRequest())
            addAll(viewModel.getAddressReadRequests())
            addAll(viewModel.getLastOperationStateReadRequest())
        }
        BluetoothService.connectGatt(
            viewModel.btDevice,
            object : BluetoothCallback(bluetoothRequests) {
                override fun onSuccess() {
                    Log.d("Success")
                    when (viewModel.lastOperationStatus.value) {
                        "address|ok" -> {
                            switchFragmentWithBackStack(
                                R.id.container,
                                ConfigurationSuccessfulFragment()
                            )
                        }
                        else -> configFailed()
                    }
                }

                override fun onFailure() {
                    configFailed()
                }

                override fun onFailToConnect() {
                    configFailed()
                }
            })
    }

    private fun configFailed() {
        Log.d("Failed")
        switchFragmentWithBackStack(
            R.id.container,
            ConfigurationFailedFragment()
        )
    }

}
