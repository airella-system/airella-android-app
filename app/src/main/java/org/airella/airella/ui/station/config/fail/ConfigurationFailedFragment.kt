package org.airella.airella.ui.station.config.fail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import kotlinx.android.synthetic.main.fragment_configuration_succesful.*
import org.airella.airella.R
import org.airella.airella.ui.OnBackPressed

class ConfigurationFailedFragment : Fragment(), OnBackPressed {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_configuration_failed, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        back_button.setOnClickListener {
            returnToConfigList()
        }
    }

    override fun onBackPressed(): Boolean {
        returnToConfigList()
        return true
    }

    private fun returnToConfigList() {
        requireActivity().supportFragmentManager.popBackStack(
            "config",
            FragmentManager.POP_BACK_STACK_INCLUSIVE
        )
    }
}
