package org.airella.airella.ui.station.config.wifi

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.wifi.WifiManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_wifi_list.*
import org.airella.airella.R

class WifiListFragment : Fragment() {

    private val wifiViewModel: WifiViewModel by activityViewModels()

    private val adapter: WifiAdapter by lazy { WifiAdapter(this, wifiViewModel.wifiList) }

    private lateinit var wifiManager: WifiManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
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

        refresh_wifi_fab.setOnClickListener {
            startWifiScan()
        }
    }

    override fun onStart() {
        super.onStart()
        startWifiScan()
    }

    fun setWifiConfig(wifiSSID: String, wifiPassword: String) {
        wifiViewModel.wifiSSID.value = wifiSSID
        wifiViewModel.wifiPassword.value = wifiPassword
    }

    @Suppress("DEPRECATION")
    private fun startWifiScan() {
        if (!wifiManager.startScan()) {
            updateWifiList()
        }
    }

    private fun updateWifiList() {
        wifiViewModel.wifiList.clear()
        wifiViewModel.wifiList.addAll(wifiManager.scanResults)
        adapter.notifyDataSetChanged()
    }

    private val wifiScanReceiver = object : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            updateWifiList()
        }
    }

}

