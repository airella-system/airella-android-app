package org.airella.airella.ui.station.btlist

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_bt_list.*
import org.airella.airella.R
import org.airella.airella.data.service.BluetoothService
import org.airella.airella.utils.Log

const val BT_ENABLE = 4

class BTListFragment : Fragment() {

    private lateinit var viewModel: BTListViewModel

    private val adapter: BTDeviceAdapter by lazy { BTDeviceAdapter() }

    private val bluetoothAdapter: BluetoothAdapter? by lazy(LazyThreadSafetyMode.NONE) {
        val bluetoothManager =
            requireContext().getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothManager.adapter
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this).get(BTListViewModel::class.java)

        return inflater.inflate(R.layout.fragment_bt_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bt_list.layoutManager = LinearLayoutManager(requireContext())
        bt_list.adapter = adapter

        viewModel.btDevicesList.observe(viewLifecycleOwner, Observer { btDevices ->
            adapter.setDevices(btDevices)
        })

        BluetoothService.isScanning.observe(viewLifecycleOwner, Observer {
            loading.visibility = if (it) View.VISIBLE else View.GONE
        })

        checkBtAndScanDevices()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == BT_ENABLE) {
            when (resultCode) {
                Activity.RESULT_OK -> checkBtAndScanDevices()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        bluetoothAdapter?.let { viewModel.stopBtScan(it) }
    }

    private fun checkBtAndScanDevices() {
        when {
            bluetoothAdapter == null -> {
                Toast.makeText(requireContext(), "BT error", Toast.LENGTH_SHORT).show()
                Log.e("Null BT adapter")
            }
            bluetoothAdapter!!.isEnabled -> viewModel.startBtScan(bluetoothAdapter!!)
            else -> startActivityForResult(
                Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE),
                BT_ENABLE
            )
        }
    }

}

