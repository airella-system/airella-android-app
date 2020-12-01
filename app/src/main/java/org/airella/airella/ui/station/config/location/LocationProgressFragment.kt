package org.airella.airella.ui.station.config.location

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

class LocationProgressFragment : Fragment() {

    private val viewModel: ConfigViewModel by activityViewModels()

    private val locationViewModel: LocationViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_configuration_progress, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val location = locationViewModel.location.value!!
        saveLocation(location.latitude.toString(), location.longitude.toString())
    }

    private fun saveLocation(latitude: String, longitude: String) {
        Log.i("Save location start")
        val bluetoothRequests: Queue<BluetoothRequest> = LinkedList<BluetoothRequest>(
            listOf(
                WriteRequest(Characteristic.LOCATION_LATITUDE, latitude),
                WriteRequest(Characteristic.LOCATION_LONGITUDE, longitude),
                WriteRequest(Characteristic.LOCATION_MANUALLY, "1"),
                WriteRequest(Characteristic.REFRESH_ACTION, RefreshAction.LOCATION.code)
            )
        ).apply {
            addAll(viewModel.getStatusReadRequest())
            addAll(viewModel.getLocationReadRequests())
            addAll(viewModel.getLastOperationStateReadRequest())
        }
        BluetoothService.connectGatt(
            viewModel.btDevice,
            object : BluetoothCallback(bluetoothRequests) {
                override fun onSuccess() {
                    Log.d("Success")
                    when (viewModel.lastOperationStatus.value) {
                        "location|ok" -> {
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
