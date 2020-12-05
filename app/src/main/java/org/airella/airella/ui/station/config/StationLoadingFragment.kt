package org.airella.airella.ui.station.config

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import org.airella.airella.MyApplication
import org.airella.airella.R
import org.airella.airella.data.bluetooth.BluetoothCallback
import org.airella.airella.data.service.BluetoothService
import org.airella.airella.ui.station.config.main.StationMainFragment
import org.airella.airella.ui.station.config.wizard.WizardFragment
import org.airella.airella.utils.FragmentUtils.switchFragment
import org.airella.airella.utils.PermissionUtils

class StationLoadingFragment : Fragment() {

    private val viewModel: ConfigViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        PermissionUtils.requestBtIfDisabled(this)
        return inflater.inflate(R.layout.fragment_station_connecting, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        BluetoothService.connectGatt(
            viewModel.btDevice,
            object : BluetoothCallback(viewModel.getFullConfig()) {
                override fun onSuccess() {
                    viewModel.isWizard.value = !viewModel.registered.value!!.isOK()
                    if (viewModel.isWizard.value!!) {
                        switchFragment(R.id.container, WizardFragment())
                    } else {
                        switchFragment(R.id.container, StationMainFragment())
                    }
                }

                override fun onFailure() {
                    onFailed()
                }

                override fun onFailToConnect() {
                    onFailed()
                }
            }
        )
    }

    private fun onFailed() {
        MyApplication.createToast("Failed to connect with station")
        activity?.finish()
    }
}