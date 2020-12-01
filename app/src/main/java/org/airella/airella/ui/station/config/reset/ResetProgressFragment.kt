package org.airella.airella.ui.station.config.reset

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import kotlinx.android.synthetic.main.fragment_configuration_progress.*
import org.airella.airella.MyApplication
import org.airella.airella.R
import org.airella.airella.config.Characteristic
import org.airella.airella.data.bluetooth.BluetoothCallback
import org.airella.airella.data.bluetooth.BluetoothRequest
import org.airella.airella.data.bluetooth.WriteRequest
import org.airella.airella.data.service.BluetoothService
import org.airella.airella.ui.station.config.ConfigViewModel
import org.airella.airella.ui.station.config.StationLoadingFragment
import org.airella.airella.utils.FragmentUtils.clearBackStack
import org.airella.airella.utils.FragmentUtils.switchFragment
import org.airella.airella.utils.Log
import java.util.*

class ResetProgressFragment : Fragment() {

    private val viewModel: ConfigViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_configuration_progress, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configuration_text.text = "Hard reset in progess"
        hardResetDevice()
    }

    private fun hardResetDevice() {
        Log.i("Hard reset started")
        Log.d("Connecting")
        val bluetoothRequests: Queue<BluetoothRequest> = LinkedList<BluetoothRequest>(
            listOf(
                WriteRequest(Characteristic.CLEAR_DATA, "")
            )
        )
        BluetoothService.connectGatt(
            viewModel.btDevice,
            object : BluetoothCallback(bluetoothRequests) {
                override fun onFailToConnect() {
                    Log.d("Failed to connect")
                }

                override fun onSuccess() {
                    MyApplication.createToast("Hard reset successful")
                    clearBackStack()
                    switchFragment(R.id.container, StationLoadingFragment())
                }

                override fun onFailure() {
                    Log.d("Hard reset failed")
                }
            })
    }
}
