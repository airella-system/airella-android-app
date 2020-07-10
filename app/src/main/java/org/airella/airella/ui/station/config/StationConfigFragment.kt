package org.airella.airella.ui.station.config

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
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.fragment_station_config.*
import org.airella.airella.R
import org.airella.airella.ui.station.wificonfig.WifiConfigFragment
import org.airella.airella.utils.Log

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
        viewModel = ViewModelProvider(this).get(StationConfigViewModel::class.java)

        viewModel.btDevice = requireArguments().getParcelable("bt_device") as BluetoothDevice

        return inflater.inflate(R.layout.fragment_station_config, container, false)
    }

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

        wifi_config.setOnClickListener { v ->
            val fragment: Fragment = WifiConfigFragment()
            switchFragment(v, fragment)
        }

        change_password.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setMessage(
                    """Co ty myślałeś że sobie tak hasło możesz zmienić? xDDDD
                        >No chyba nie""".trimMargin(">")
                )
                .show()
        }

        hard_reset.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("HARD RESET")
                .setMessage("Are you sure you want to reset all configuration?")
                .setPositiveButton(android.R.string.yes) { _, _ ->
                    viewModel.hardResetDevice(requireContext())
                }
                .setNegativeButton(android.R.string.no, null)
                .show()
        }
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

    private fun switchFragment(view: View, fragment: Fragment) {
        val transaction: FragmentTransaction =
            (view.context as FragmentActivity).supportFragmentManager.beginTransaction()

        val bundle = Bundle()
        bundle.putParcelable("bt_device", viewModel.btDevice)
        fragment.arguments = bundle

        transaction.replace(R.id.container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    private fun updateBondState() {
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
        change_password.isEnabled = viewModel.isBonded()
        hard_reset.isEnabled = viewModel.isBonded()
    }

}

