package org.airella.airella.ui.station.config.success

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import kotlinx.android.synthetic.main.fragment_configuration_succesful.*
import org.airella.airella.R

class ConfigurationSuccessfulFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_configuration_succesful, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        back_button.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack(
                "config",
                FragmentManager.POP_BACK_STACK_INCLUSIVE
            )
        }
    }

}
