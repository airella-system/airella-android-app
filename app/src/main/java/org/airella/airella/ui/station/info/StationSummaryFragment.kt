package org.airella.airella.ui.station.info

import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.synthetic.main.fragment_station_summary.*
import org.airella.airella.R
import org.airella.airella.data.service.BluetoothService
import org.airella.airella.data.service.StationService
import org.airella.airella.ui.station.config.StationConfigActivity
import org.airella.airella.utils.Log
import org.airella.airella.utils.PermissionUtils

class StationSummaryFragment : Fragment() {

    private val viewModel: StationInfoViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_station_summary, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        name.text = viewModel.station.name
        aqi.text = viewModel.station.aqi.toString()
        address.text = viewModel.station.address.toString()

        station_connect_button.setOnClickListener {
            if (PermissionUtils.requestBtIfDisabled(this)) return@setOnClickListener
            Toast.makeText(requireContext(), "Connecting to a station", Toast.LENGTH_SHORT).show()

            connectToDevice()
        }

        remove_station_button.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Are you sure to remove a station?")
                .setPositiveButton("Try again") { _, _ ->
                    Toast.makeText(requireContext(), "Removing a station", Toast.LENGTH_SHORT)
                        .show()
                    StationService.removeStation(viewModel.station.id).subscribe({
                        Toast.makeText(requireContext(), "Removed a station", Toast.LENGTH_LONG)
                            .show()
                        activity?.finish()
                    }, {
                        Toast.makeText(
                            requireContext(),
                            "Failed to remove a station",
                            Toast.LENGTH_LONG
                        ).show()
                    })
                }
                .setNegativeButton(R.string.cancel, null)
                .show()
        }
    }

    private fun connectToDevice() {
        StationService.getStatistic(viewModel.station.id, "btMacAddress")
            .map { it.values[0].value }
            .subscribe({ btMacAddress ->
                Log.e("Connecting to $btMacAddress")
                val btDevice = BluetoothService.getDeviceByMac(btMacAddress)
                btDevice.connectGatt(requireContext(), false, object : BluetoothGattCallback() {
                    override fun onConnectionStateChange(
                        gatt: BluetoothGatt,
                        status: Int,
                        newState: Int
                    ) {
                        super.onConnectionStateChange(gatt, status, newState)
                        gatt.close()
                        if (newState == BluetoothGatt.STATE_CONNECTED) {
                            Log.i("Connected to station")
                            requireActivity().runOnUiThread {
                                val intent =
                                    Intent(requireContext(), StationConfigActivity::class.java)
                                intent.putExtra("bt_device", btDevice)
                                ContextCompat.startActivity(requireContext(), intent, null)
                            }
                        } else if (newState != BluetoothGatt.STATE_DISCONNECTED) {
                            Toast.makeText(
                                requireContext(),
                                "Connecting to station",
                                Toast.LENGTH_LONG
                            )
                                .show()
                            Log.w("Failed to connect to station")
                        }
                    }
                })
            }, {
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle("No internet")
                    .setMessage("Internet is required to get station MAC address")
                    .setPositiveButton("Try again") { _, _ ->
                        connectToDevice()
                    }
                    .setNegativeButton(R.string.cancel, null)
                    .show()
            })
    }

}