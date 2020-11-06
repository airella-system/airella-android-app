package org.airella.airella.ui.station.config.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import kotlinx.android.synthetic.main.fragment_station_status.*
import org.airella.airella.R
import org.airella.airella.ui.station.config.ConfigViewModel
import org.airella.airella.utils.PermissionUtils

class StationStatusFragment : Fragment() {

    private val viewModel: ConfigViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        PermissionUtils.requestBtIfDisabled(this)

        return inflater.inflate(R.layout.fragment_station_status, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.registered.observe(viewLifecycleOwner, {
            registered.text = it.code
        })

        viewModel.apiConnection.observe(viewLifecycleOwner, {
            api_connection.text = it.code
        })

        viewModel.airSensor.observe(viewLifecycleOwner, {
            air_sensor.text = it.code
        })

        viewModel.gps.observe(viewLifecycleOwner, {
            gps.text = it.code
        })

        viewModel.heater.observe(viewLifecycleOwner, {
            heater.text = it.code
        })

        viewModel.powerSensor.observe(viewLifecycleOwner, {
            power_sensor.text = it.code
        })

        viewModel.weatherStatus.observe(viewLifecycleOwner, {
            weather_sensor.text = it.code
        })
    }


}

