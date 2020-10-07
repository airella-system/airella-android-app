package org.airella.airella.ui.home.station

import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.fragment_station_summary.*
import org.airella.airella.R
import org.airella.airella.data.model.sensor.Station
import org.airella.airella.data.service.BluetoothService
import org.airella.airella.data.service.StationService
import org.airella.airella.ui.station.config.StationConfigFragment
import org.airella.airella.utils.FragmentUtils.switchFragmentWithBackStack
import org.airella.airella.utils.Log
import org.airella.airella.utils.PermissionUtils

class StationSummaryFragment : Fragment() {

    private lateinit var viewModel: StationSummaryViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        PermissionUtils.requestBtIfDisabled(this)

        viewModel = ViewModelProvider(this).get(StationSummaryViewModel::class.java)
        viewModel.station = requireArguments().getSerializable("station") as Station

        return inflater.inflate(R.layout.fragment_station_summary, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        name.text = viewModel.station.name
        aqi.text = viewModel.station.aqi.toString()
        address.text = viewModel.station.address.toString()

        //TODO check permission
        StationService.getStatistic(viewModel.station.id, "btMacAddress").subscribe({
            // TODO()
//            viewModel.stationBtMacAddress = it.values.fold()
        }, {
            Log.e(it.toString())
        })

        station_connect_button.setOnClickListener {
            Toast.makeText(requireContext(), "Connecting to a station", Toast.LENGTH_SHORT).show()

            //TODO checkBT
//            val btDevice =  BluetoothService.getDeviceByMac(viewModel.stationBtMacAddress)
            val btDevice = BluetoothService.getDeviceByMac("FC:F5:C4:2F:45:B6")
            btDevice.connectGatt(requireContext(), false, object : BluetoothGattCallback() {
                override fun onConnectionStateChange(
                    gatt: BluetoothGatt,
                    status: Int,
                    newState: Int
                ) {
                    super.onConnectionStateChange(gatt, status, newState)
                    gatt.disconnect()
                    if (newState == BluetoothGatt.STATE_CONNECTED) {
                        Log.i("Connected to station")
                        requireActivity().runOnUiThread {
                            val configFragment: Fragment = StationConfigFragment()
                            val bundle = Bundle()
                            bundle.putParcelable("bt_device", btDevice)
                            configFragment.arguments = bundle

                            switchFragmentWithBackStack(R.id.container, configFragment)
                        }
                    } else if (newState != BluetoothGatt.STATE_DISCONNECTED) {
                        Toast.makeText(requireContext(), "Connecting to station", Toast.LENGTH_LONG)
                            .show()
                        Log.w("Failed to connect to station")
                    }
                }
            })
        }

        remove_station_button.setOnClickListener {
            Toast.makeText(requireContext(), "Removing a station", Toast.LENGTH_SHORT).show()
            StationService.removeStation(viewModel.station.id).subscribe({
                Toast.makeText(requireContext(), "Removed a station", Toast.LENGTH_LONG).show()
                requireActivity().finish()
            }, {
                Toast.makeText(requireContext(), "Failed to remove a station", Toast.LENGTH_LONG)
                    .show()
            })
        }

    }
}