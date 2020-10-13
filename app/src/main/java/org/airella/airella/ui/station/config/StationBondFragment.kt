package org.airella.airella.ui.station.config

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import kotlinx.android.synthetic.main.fragment_station_bond.*
import org.airella.airella.R
import org.airella.airella.utils.PermissionUtils

class StationBondFragment() : Fragment() {

    private val viewModel: ConfigViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_station_bond, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bond_device_button.setOnClickListener {
            if (PermissionUtils.requestBtIfDisabled(this)) return@setOnClickListener
            viewModel.btDevice.createBond()
        }
    }
}