package org.airella.airella.ui.station.config.name

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import kotlinx.android.synthetic.main.fragment_location.btn_continue
import kotlinx.android.synthetic.main.fragment_station_name.*
import org.airella.airella.R
import org.airella.airella.ui.station.config.ConfigViewModel
import org.airella.airella.ui.station.config.internet.InternetChooseFragment
import org.airella.airella.utils.FragmentUtils.switchFragmentWithBackStack

class StationNameFragment : Fragment() {

    private val configViewModel: ConfigViewModel by activityViewModels()

    private val stationNameViewModel: StationNameViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_station_name, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        station_name_edit.setText(configViewModel.stationName.value)

        btn_continue.setText(if (configViewModel.isWizard()) R.string.action_continue else R.string.action_save)

        btn_continue.setOnClickListener {
            stationNameViewModel.stationName.value = station_name_edit.text.toString()
            if (configViewModel.isWizard()) {
                switchFragmentWithBackStack(R.id.container, InternetChooseFragment())
            } else {
                switchFragmentWithBackStack(R.id.container, StationNameProgressFragment())
            }
        }
    }

}
