package org.airella.airella.ui.station.btlist

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_bt_list.*
import org.airella.airella.R
import org.airella.airella.data.service.BluetoothService
import org.airella.airella.utils.Log


const val BT_ENABLE = 4

const val REQUEST_FINE_LOCATION = 99

class BTListFragment : Fragment() {

    private lateinit var viewModel: BTListViewModel

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
        bt_list.adapter = viewModel.adapter

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
        if (!checkLocationPermission()) return

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

    private fun checkLocationPermission(): Boolean {
        return if (
            ContextCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            location_denied.visibility = View.GONE
            if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)
            ) {
                AlertDialog.Builder(requireContext())
                    .setTitle("Location")
                    .setMessage(R.string.location_permission_required_scan)
                    .setPositiveButton(
                        "Ok"
                    ) { _, _ ->
                        requestPermissions(
                            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                            REQUEST_FINE_LOCATION
                        )
                    }
                    .create()
                    .show()
            } else {
                requestPermissions(
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    REQUEST_FINE_LOCATION
                )
            }
            false
        } else {
            true
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            REQUEST_FINE_LOCATION -> {
                if (grantResults.isNotEmpty()
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                ) {
                    if (ContextCompat.checkSelfPermission(
                            requireContext(),
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        checkBtAndScanDevices()
                    }
                } else {
                    // user checked Never ask again
                    if (!shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                        location_denied.visibility = View.VISIBLE
                    } else {
                        checkBtAndScanDevices()
                    }
                }
                return
            }
        }
    }

}

