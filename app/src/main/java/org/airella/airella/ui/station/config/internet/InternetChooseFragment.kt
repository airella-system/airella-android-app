package org.airella.airella.ui.station.config.internet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import kotlinx.android.synthetic.main.fragment_internet_choose.*
import org.airella.airella.R
import org.airella.airella.ui.station.config.ConfigViewModel
import org.airella.airella.ui.station.config.gsm.GsmConfigFragment
import org.airella.airella.ui.station.config.wifi.WifiListFragment
import org.airella.airella.utils.FragmentUtils.switchFragmentWithBackStack

class InternetChooseFragment : Fragment() {

    private val configViewModel: ConfigViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_internet_choose, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        wifi_button.setOnClickListener {
            switchFragmentWithBackStack(R.id.container, WifiListFragment())
        }

        if (configViewModel.isWizard()) {
            gsm_button.isEnabled = false
            text_disabled_gsm.visibility = View.VISIBLE
        } else {
            gsm_button.setOnClickListener {
                switchFragmentWithBackStack(R.id.container, GsmConfigFragment())
            }
        }

    }

}