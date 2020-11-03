package org.airella.airella.ui.station.config.success

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import kotlinx.android.synthetic.main.fragment_configuration_succesful.*
import org.airella.airella.R
import org.airella.airella.ui.OnBackPressed

class ConfigurationSuccessfulFragment : Fragment(), OnBackPressed {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_configuration_succesful, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.getString("success_text")?.let {
            main_text.text = it
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
        requireActivity().supportFragmentManager.popBackStack(
            "config",
            FragmentManager.POP_BACK_STACK_INCLUSIVE
        )
    }

}
