package org.airella.airella.ui.station.config;

import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import org.airella.airella.R
import org.airella.airella.ui.OnBackPressed
import org.airella.airella.ui.station.config.main.StationMainFragment
import org.airella.airella.utils.Log

class StationActivity : AppCompatActivity() {

    private val btBondBroadcastReceiver = object : BroadcastReceiver() {

        override fun onReceive(context: Context?, intent: Intent) {
            when (intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, BluetoothDevice.ERROR)) {
                BluetoothDevice.BOND_NONE -> Log.w("Bonding Failed")
                BluetoothDevice.BOND_BONDING -> Log.d("Bonding...")
                BluetoothDevice.BOND_BONDED -> {
                    unregisterReceiver(this)
                    Log.d("Bonded!")
                    viewModel.getStationConfig()
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.container, StationMainFragment())
                        .commitNow()
                }
            }
        }
    }

    private val viewModel: ConfigViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_container)

        if (savedInstanceState == null) {
            viewModel.btDevice = intent.extras!!.getParcelable("bt_device")!!

            if (viewModel.btDevice.bondState == BluetoothDevice.BOND_BONDED) {
                viewModel.getStationConfig()
                supportFragmentManager.beginTransaction()
                    .replace(R.id.container, StationMainFragment())
                    .commitNow()
            } else {
                registerReceiver(
                    btBondBroadcastReceiver,
                    IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED)
                )

                supportFragmentManager.beginTransaction()
                    .replace(R.id.container, StationBondFragment())
                    .commitNow()
            }

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            unregisterReceiver(btBondBroadcastReceiver)
        } catch (_: IllegalArgumentException) {
        }
    }


    override fun onBackPressed() {
        val fragment =
            this.supportFragmentManager.findFragmentById(R.id.container)
        if ((fragment as? OnBackPressed)?.onBackPressed() != true) {
            super.onBackPressed()
        }
    }
}