package org.airella.airella.ui.station.config.location

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
        val bluetoothRequests: Queue<BluetoothRequest> = LinkedList(
            listOf(
                WriteRequest(Config.LOCATION_LATITUDE_UUID, latitude),
                WriteRequest(Config.LOCATION_LONGITUDE_UUID, longitude),
                WriteRequest(Config.LOCATION_MANUALLY_UUID, "1"),
                WriteRequest(Config.REFRESH_ACTION_UUID, Config.LOCATION_ACTION)
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
