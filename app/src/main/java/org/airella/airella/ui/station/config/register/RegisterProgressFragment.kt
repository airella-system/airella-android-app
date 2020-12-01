package org.airella.airella.ui.station.config.register

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import kotlinx.android.synthetic.main.fragment_configuration_progress.*
import org.airella.airella.R
import org.airella.airella.config.Characteristic
import org.airella.airella.config.RefreshAction
import org.airella.airella.data.api.ApiManager
import org.airella.airella.data.bluetooth.BluetoothCallback
import org.airella.airella.data.bluetooth.BluetoothRequest
import org.airella.airella.data.bluetooth.WriteRequest
import org.airella.airella.data.service.AuthService
import org.airella.airella.data.service.BluetoothService
import org.airella.airella.ui.OnBackPressed
import org.airella.airella.ui.station.config.ConfigViewModel
import org.airella.airella.ui.station.config.fail.ConfigurationFailedFragment
import org.airella.airella.ui.station.config.success.ConfigurationSuccessfulFragment
import org.airella.airella.utils.FragmentUtils.switchFragmentWithBackStack
import org.airella.airella.utils.Log
import java.util.*

class RegisterProgressFragment : Fragment(), OnBackPressed {

    private val viewModel: ConfigViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_configuration_progress, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configuration_text.setText(R.string.registration_in_progress)
        registerStation(ApiManager.baseApiUrl)
    }

    private fun registerStation(apiUrl: String) {
        Log.i("Register station start")
        val bluetoothRequests: Queue<BluetoothRequest> = LinkedList<BluetoothRequest>(
            listOf(
                WriteRequest(
                    Characteristic.REGISTRATION_TOKEN,
                    AuthService.getUser().stationRegistrationToken
                ),
                WriteRequest(Characteristic.API_URL, apiUrl),
                WriteRequest(Characteristic.REFRESH_ACTION, RefreshAction.REGISTER.code)
            )
        ).apply {
            addAll(viewModel.getStatusReadRequest())
        }
        BluetoothService.connectGatt(
            viewModel.btDevice,
            object : BluetoothCallback(bluetoothRequests) {
                override fun onSuccess() {
                    when {
                        viewModel.registered.value!!.isOK() -> {
                            Log.d("Success")
                            val configurationSuccessfulFragment =
                                ConfigurationSuccessfulFragment()
                            configurationSuccessfulFragment.arguments =
                                bundleOf(
                                    Pair(
                                        "success_text",
                                        getString(R.string.registration_successful)
                                    )
                                )
                            switchFragmentWithBackStack(
                                R.id.container,
                                configurationSuccessfulFragment
                            )
                        }
                        else -> configFailed()
                    }
                }

                override fun onFailure() {
                    configFailed()
                }

                override fun onFailToConnect() {
                    configFailed()
                }
            })
    }

    private fun configFailed() {
        Log.d("Failed")
        switchFragmentWithBackStack(
            R.id.container,
            ConfigurationFailedFragment()
        )
    }
}
