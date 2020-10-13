package org.airella.airella.ui.station.config.list

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.synthetic.main.fragment_station_config.*
import org.airella.airella.MyApplication
import org.airella.airella.R
import org.airella.airella.data.bluetooth.BluetoothCallback
import org.airella.airella.data.bluetooth.BluetoothRequest
import org.airella.airella.data.bluetooth.WriteRequest
import org.airella.airella.ui.station.config.ConfigViewModel
import org.airella.airella.ui.station.config.address.AddressFragment
import org.airella.airella.ui.station.config.internet.InternetChooseFragment
import org.airella.airella.ui.station.config.location.LocationFragment
import org.airella.airella.ui.station.config.name.StationNameFragment
import org.airella.airella.ui.station.config.register.RegisterProgressFragment
import org.airella.airella.config.Characteristic
import org.airella.airella.utils.FragmentUtils.switchFragmentWithBackStack
import org.airella.airella.utils.Log
import org.airella.airella.utils.PermissionUtils
import java.util.*

class StationConfigFragment : Fragment() {

    private val btBondBroadcastReceiver = object : BroadcastReceiver() {

        override fun onReceive(context: Context?, intent: Intent) {
            updateBondState()
            when (intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, BluetoothDevice.ERROR)) {
                BluetoothDevice.BOND_NONE -> Log.w("Bonding Failed")
                BluetoothDevice.BOND_BONDING -> Log.d("Bonding...")
                BluetoothDevice.BOND_BONDED -> Log.d("Bonded!")
            }
        }
    }

    private val viewModel: ConfigViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        PermissionUtils.requestBtIfDisabled(this)

        return inflater.inflate(R.layout.fragment_station_config, container, false)
    }

    @SuppressLint("InflateParams", "SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        updateBondState()

        viewModel.stationName.observe(viewLifecycleOwner, {
            station_name_view.text = it
        })

        viewModel.connectionType.observe(viewLifecycleOwner, {
            station_connection_type_view.text = it.toString()
        })

        viewModel.stationWifiSSID.observe(viewLifecycleOwner, {
            station_wifi_view.text = it
        })

        fun setAddress1() {
            station_address_view_1.text =
                "${viewModel.stationStreet.value} ${viewModel.stationHouseNo.value}"
        }

        viewModel.stationStreet.observe(viewLifecycleOwner, {
            setAddress1()
        })

        viewModel.stationHouseNo.observe(viewLifecycleOwner, {
            setAddress1()
        })

        fun setAddress2() {
            station_address_view_2.text =
                "${viewModel.stationCity.value}, ${viewModel.stationCountry.value}"
        }

        viewModel.stationCountry.observe(viewLifecycleOwner, {
            setAddress2()
        })

        viewModel.stationCity.observe(viewLifecycleOwner, {
            setAddress2()
        })

        fun setLocation() {
            station_location_view.text =
                "${viewModel.stationLatitude.value}, ${viewModel.stationLongitude.value}"
        }

        viewModel.stationLatitude.observe(viewLifecycleOwner, {
            setLocation()
        })

        viewModel.stationLongitude.observe(viewLifecycleOwner, {
            setLocation()
        })

//        bond_device.setOnClickListener {
//            viewModel.btDevice.createBond()
//            updateBondState()
//            requireActivity().registerReceiver(
//                btBondBroadcastReceiver,
//                IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED)
//            )
//        }

        station_name_edit_button.setOnClickListener {
            goToConfigFragment(StationNameFragment())
        }

        station_wifi_edit_button.setOnClickListener {
            goToConfigFragment(InternetChooseFragment())
        }

        station_address_edit_button.setOnClickListener {
            goToConfigFragment(AddressFragment())
        }

        station_location_edit_button.setOnClickListener {
            goToConfigFragment(LocationFragment())
        }

        register_station.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Register station")
                .setMessage("Are you sure you want to register station?")
                .setPositiveButton(android.R.string.yes) { _, _ ->
                    goToConfigFragment(RegisterProgressFragment())
                }
                .setNegativeButton(R.string.cancel, null)
                .show()
        }

        hard_reset.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Hard Reset")
                .setMessage("Are you sure you want to reset all configuration?")
                .setPositiveButton(android.R.string.yes) { _, _ ->
                    hardResetDevice()
                }
                .setNegativeButton(R.string.cancel, null)
                .show()
        }
    }

    private fun goToConfigFragment(newFragment: Fragment) {
        switchFragmentWithBackStack(R.id.container, newFragment, "config")
    }

    override fun onResume() {
        super.onResume()
        updateBondState()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        try {
            requireActivity().unregisterReceiver(btBondBroadcastReceiver)
        } catch (_: IllegalArgumentException) {
        }
    }

    private fun updateBondState() {
        if (PermissionUtils.requestBtIfDisabled(this)) return

//        bond_status.text = when (viewModel.btDevice.bondState) {
//            BluetoothDevice.BOND_NONE -> "NOT BONDED"
//            BluetoothDevice.BOND_BONDING -> "BONDING"
//            else -> "BONDED"
//        }
//        bond_status.setTextColor(
//            when (viewModel.btDevice.bondState) {
//                BluetoothDevice.BOND_NONE -> Color.RED
//                BluetoothDevice.BOND_BONDING -> Color.rgb(255, 140, 0)
//                else -> Color.GREEN
//            }
//        )

//        bond_device.isEnabled = viewModel.btDevice.bondState == BluetoothDevice.BOND_NONE

        station_name_edit_button.isEnabled = isBonded()
        station_wifi_edit_button.isEnabled = isBonded()
        station_address_edit_button.isEnabled = isBonded()
        station_location_edit_button.isEnabled = isBonded()
        register_station.isEnabled = isBonded()
        hard_reset.isEnabled = isBonded()
    }

    private fun isBonded() = viewModel.btDevice.bondState == BluetoothDevice.BOND_BONDED

    private fun hardResetDevice() {
        Log.i("Hard reset started")
        MyApplication.setStatus("Connecting")
        val bluetoothRequests: Queue<BluetoothRequest> = LinkedList(
            listOf(
                WriteRequest(Characteristic.CLEAR_DATA, "")
            )
        )

        viewModel.btDevice.connectGatt(
            context,
            false,
            object : BluetoothCallback(bluetoothRequests) {
                override fun onFailToConnect() = MyApplication.setStatus("Failed to connect")
                override fun onSuccess() = MyApplication.setStatus("Hard reset successful")
                override fun onFailure() = MyApplication.setStatus("Hard reset failed")
            })
    }
}

