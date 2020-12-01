package org.airella.airella.ui.station.config.wizard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import kotlinx.android.synthetic.main.fragment_station_wizard.*
import org.airella.airella.R
import org.airella.airella.ui.station.config.ConfigViewModel
import org.airella.airella.ui.station.config.name.StationNameFragment
import org.airella.airella.utils.FragmentUtils.switchFragment
import org.airella.airella.utils.PermissionUtils

class WizardFragment : Fragment() {

    private val viewModel: ConfigViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        PermissionUtils.requestBtIfDisabled(this)
        return inflater.inflate(R.layout.fragment_station_wizard, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btn_continue.setOnClickListener {
            switchFragment(R.id.container, StationNameFragment())
        }
    }
}