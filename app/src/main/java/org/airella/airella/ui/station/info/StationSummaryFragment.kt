package org.airella.airella.ui.station.info

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.synthetic.main.fragment_station_summary.*
import org.airella.airella.MyApplication.Companion.createToast
import org.airella.airella.R
import org.airella.airella.data.service.BluetoothService
import org.airella.airella.data.service.StationService
import org.airella.airella.ui.station.config.StationActivity
import org.airella.airella.utils.Log
import org.airella.airella.utils.PermissionUtils
import java.util.*

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

        updateStationInfo()

        station_connect_button.setOnClickListener {
            if (PermissionUtils.requestBtIfDisabled(this)) return@setOnClickListener
            connectToDevice()
        }

        remove_station_button.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Are you sure to remove a station?")
                .setPositiveButton(android.R.string.yes) { _, _ ->
                    createToast("Removing a station")
                    StationService.removeStation(viewModel.station.id).subscribe({
                        createToast("Removed a station")
                        activity?.finish()
                    }, {
                        createToast("Failed to remove a station")
                    })
                }
                .setNegativeButton(R.string.cancel, null)
                .show()
        }
    }

    override fun onResume() {
        super.onResume()
        StationService.getStation(viewModel.station.id).subscribe({ station ->
            viewModel.station = station
            updateStationInfo()
        }, { error ->
            Log.e(error.toString())
        })
    }

    private fun updateStationInfo() {
        name.text = viewModel.station.name
        aqi.text = viewModel.station.aqi.toString()
        address.text = viewModel.station.address.toString()
    }

    private fun connectToDevice() {
        StationService.getStatistic(viewModel.station.id, "btMacAddress")
            .map { it.values[0].value }
            .subscribe({ btMacAddress ->
                Log.d("Connecting to $btMacAddress")
                val btDevice = BluetoothService.getDeviceByMac(btMacAddress)
                val intent =
                    Intent(requireContext(), StationActivity::class.java)
                intent.putExtra("bt_device", btDevice)
                ContextCompat.startActivity(requireContext(), intent, null)
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