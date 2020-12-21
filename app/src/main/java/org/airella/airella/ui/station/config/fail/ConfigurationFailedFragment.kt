package org.airella.airella.ui.station.config.fail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import kotlinx.android.synthetic.main.fragment_configuration_failed.*
import kotlinx.android.synthetic.main.fragment_configuration_succesful.back_button
import org.airella.airella.R
import org.airella.airella.ui.OnBackPressed
import org.airella.airella.ui.station.config.ConfigViewModel

class ConfigurationFailedFragment : Fragment(), OnBackPressed {

    private val configViewModel: ConfigViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_configuration_failed, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.getString("fail_text")?.let {
            secondary_text.text = it
        }

        retry_button.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        back_button.setOnClickListener {
            returnToConfigList()
        }
    }

    override fun onBackPressed(): Boolean {
        returnToConfigList()
        return true
    }

    private fun returnToConfigList() {
        if (configViewModel.isWizard()) {
            requireActivity().supportFragmentManager.popBackStack()
            requireActivity().supportFragmentManager.popBackStack()
        } else {
            requireActivity().supportFragmentManager.popBackStack(
                "config",
                FragmentManager.POP_BACK_STACK_INCLUSIVE
            )
        }
    }
}
