package org.airella.airella.ui.station.btlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_bt_list.*
import org.airella.airella.R
import org.airella.airella.data.service.BluetoothScanService
import org.airella.airella.utils.PermissionUtils

class BTListFragment : Fragment() {

    private lateinit var viewModel: BTListViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        PermissionUtils.requestBtIfDisabled(this)

        viewModel = ViewModelProvider(this).get(BTListViewModel::class.java)

        return inflater.inflate(R.layout.fragment_bt_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bt_list.layoutManager = LinearLayoutManager(requireContext())
        bt_list.adapter = viewModel.adapter

        BluetoothScanService.isScanning.observe(viewLifecycleOwner, {
            loading.visibility = if (it) View.VISIBLE else View.GONE
        })
    }

    override fun onPause() {
        super.onPause()
        viewModel.stopBtScan()
    }

    override fun onResume() {
        super.onResume()
        if (PermissionUtils.requestBtIfDisabled(this)) return
        viewModel.startBtScan(this)
    }



}

