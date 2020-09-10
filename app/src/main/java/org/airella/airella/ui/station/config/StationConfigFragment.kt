package org.airella.airella.ui.station.config

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.fragment_station_config.*
import org.airella.airella.R
import org.airella.airella.data.service.AuthService
import org.airella.airella.ui.station.address.AddressFragment
import org.airella.airella.ui.station.location.LocationFragment
import org.airella.airella.ui.station.wifilist.WifiListFragment
import org.airella.airella.utils.Config
import org.airella.airella.utils.FragmentUtils.switchFragmentWithBackStack
import org.airella.airella.utils.Log
import org.airella.airella.utils.PermissionUtils.requestBtIfDisabled

class StationConfigFragment : Fragment() {

    private val btBondBroadcastReceiver = object : BroadcastReceiver() {

        override fun onReceive(context: Context?, intent: Intent) {
            updateBondState()
            when (intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, BluetoothDevice.ERROR)) {
                BluetoothDevice.BOND_NONE -> Log.i("Bonding Failed")
                BluetoothDevice.BOND_BONDING -> Log.i("Bonding...")
                BluetoothDevice.BOND_BONDED -> Log.i("Bonded!")
            }
        }
    }

    private lateinit var viewModel: StationConfigViewModel


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        requestBtIfDisabled()

        viewModel = ViewModelProvider(this).get(StationConfigViewModel::class.java)

        viewModel.btDevice = requireArguments().getParcelable("bt_device") as BluetoothDevice

        return inflater.inflate(R.layout.fragment_station_config, container, false)
    }

    @SuppressLint("InflateParams")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        updateBondState()

        bond_device.setOnClickListener {
            viewModel.btDevice.createBond()
            updateBondState()
            requireActivity().registerReceiver(
                btBondBroadcastReceiver,
                IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED)
            )
        }

        wifi_config.setOnClickListener {
            goToConfigFragment(WifiListFragment())
        }

        address_config.setOnClickListener {
            goToConfigFragment(AddressFragment())
        }


        location_config.setOnClickListener {
            goToConfigFragment(LocationFragment())
        }

        register_station.setOnClickListener {
            val form =
                requireActivity().layoutInflater.inflate(R.layout.view_device_register_config, null)
            form.findViewById<EditText>(R.id.apiUrl).setText(Config.DEFAULT_API_URL)
            form.findViewById<EditText>(R.id.registrationToken)
                .setText(AuthService.getUser().stationRegistrationToken)
            AlertDialog.Builder(requireContext())
                .setMessage("Wifi config")
                .setView(form)
                .setPositiveButton(R.string.action_save) { _, _ ->
                    val stationName = form.findViewById<EditText>(R.id.stationName).text.toString()
                    val apiUrl = form.findViewById<EditText>(R.id.apiUrl).text.toString()
                    viewModel.registerStation(requireContext(), stationName, apiUrl)
                }
                .setNegativeButton(R.string.cancel, null)
                .show()
        }

        hard_reset.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Hard Reset")
                .setMessage("Are you sure you want to reset all configuration?")
                .setPositiveButton(android.R.string.yes) { _, _ ->
                    viewModel.hardResetDevice(requireContext())
                }
                .setNegativeButton(R.string.cancel, null)
                .show()
        }
    }

    private fun goToConfigFragment(newFragment: Fragment) {
        val bundle = Bundle()
        bundle.putParcelable("bt_device", viewModel.btDevice)

        newFragment.arguments = bundle
        switchFragmentWithBackStack(R.id.container, newFragment)
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
        if (requestBtIfDisabled()) return

        bond_status.text = when (viewModel.btDevice.bondState) {
            BluetoothDevice.BOND_NONE -> "NOT BONDED"
            BluetoothDevice.BOND_BONDING -> "BONDING"
            else -> "BONDED"
        }
        bond_status.setTextColor(
            when (viewModel.btDevice.bondState) {
                BluetoothDevice.BOND_NONE -> Color.RED
                BluetoothDevice.BOND_BONDING -> Color.rgb(255, 140, 0)
                else -> Color.GREEN
            }
        )

        bond_device.isEnabled = viewModel.btDevice.bondState == BluetoothDevice.BOND_NONE

        wifi_config.isEnabled = viewModel.isBonded()
        address_config.isEnabled = viewModel.isBonded()
        location_config.isEnabled = viewModel.isBonded()
        hard_reset.isEnabled = viewModel.isBonded()
    }

}

