package org.airella.airella.ui.station.wifilist

import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.wifi.WifiManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_wifi_list.*
import org.airella.airella.R

class WifiListFragment : Fragment() {

    private lateinit var viewModel: WifiListViewModel

    private val adapter: WifiAdapter by lazy { WifiAdapter(this, viewModel.wifiList) }

    private val toast by lazy { Toast.makeText(requireContext(), "", Toast.LENGTH_LONG) }

    private lateinit var wifiManager: WifiManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this).get(WifiListViewModel::class.java)

        viewModel.btDevice = requireArguments().getParcelable("bt_device") as BluetoothDevice
        wifiManager =
            requireContext().applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager

        return inflater.inflate(R.layout.fragment_wifi_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        wifi_list.layoutManager = LinearLayoutManager(requireContext())
        wifi_list.adapter = adapter

        val intentFilter = IntentFilter()
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
        requireContext().registerReceiver(wifiScanReceiver, intentFilter)

        updateWifiList()
    }

    override fun onStart() {
        super.onStart()
        startWifiScan()
    }

    fun saveWifiConfig(ssid: String, password: String) {
        viewModel.saveWiFiConfig(
            this,
            ssid,
            password
        )
    }

    @Suppress("DEPRECATION")
    fun startWifiScan() {
        if (!wifiManager.startScan()) {
            updateWifiList()
        }
    }

    fun updateWifiList() {
        viewModel.wifiList.clear()
        viewModel.wifiList.addAll(wifiManager.scanResults)
        adapter.notifyDataSetChanged()
    }

    private val wifiScanReceiver = object : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            updateWifiList()
        }
    }


}

