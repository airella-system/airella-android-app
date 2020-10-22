package org.airella.airella.ui.station.config.gsm

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import kotlinx.android.synthetic.main.fragment_gsm_config.*
import kotlinx.android.synthetic.main.fragment_location.btn_continue
import org.airella.airella.R
import org.airella.airella.utils.FragmentUtils.switchFragmentWithBackStack

class GsmConfigFragment : Fragment() {

    private val gsmViewModel: GsmViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_gsm_config, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btn_continue.setText(R.string.action_save)

        btn_continue.setOnClickListener {
            gsmViewModel.apn.value = apn_edit.text.toString()
            gsmViewModel.username.value = gsm_username_edit.text.toString()
            gsmViewModel.password.value = gsm_password_edit.text.toString()
            gsmViewModel.extenderUrl.value = gsm_extender_edit.text.toString()
            switchFragmentWithBackStack(R.id.container, GsmProgressFragment())
        }
    }
}