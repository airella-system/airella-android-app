package org.airella.airella.ui.station.config.gsm

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import org.airella.airella.R
import org.airella.airella.config.Characteristic
import org.airella.airella.config.InternetConnectionType
import org.airella.airella.config.RefreshAction
import org.airella.airella.data.bluetooth.BluetoothCallback
import org.airella.airella.data.bluetooth.BluetoothRequest
import org.airella.airella.data.bluetooth.WriteRequest
import org.airella.airella.data.service.BluetoothService
import org.airella.airella.ui.OnBackPressed
import org.airella.airella.ui.station.config.ConfigViewModel
import org.airella.airella.ui.station.config.success.ConfigurationSuccessfulFragment
import org.airella.airella.utils.FragmentUtils.btConnectionFailed
import org.airella.airella.utils.FragmentUtils.internetConnectionFailed
import org.airella.airella.utils.FragmentUtils.switchFragmentWithBackStack
import org.airella.airella.utils.Log
import java.util.*

class GsmProgressFragment : Fragment(), OnBackPressed {

    private val viewModel: ConfigViewModel by activityViewModels()

    private val gsmViewModel: GsmViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_configuration_progress, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val apn = gsmViewModel.apn.value!!
        val gmsUsername = gsmViewModel.username.value!!
        val gsmPassword = gsmViewModel.password.value!!
        val gsmExtenderUrl = gsmViewModel.extenderUrl.value!!
        saveGsm(apn, gmsUsername, gsmPassword, gsmExtenderUrl)
    }

    private fun saveGsm(
        apn: String,
        gsmUsername: String,
        gsmPassword: String,
        gsmExtenderUrl: String
    ) {
        Log.i("Save gsm config start")
        val bluetoothRequests: Queue<BluetoothRequest> = LinkedList<BluetoothRequest>(
            listOf(
                WriteRequest(
                    Characteristic.INTERNET_CONNECTION_TYPE,
                    InternetConnectionType.GSM.code
                ),
                WriteRequest(Characteristic.GSM_CONFIG, """"$apn","$gsmUsername","$gsmPassword""""),
                WriteRequest(Characteristic.GSM_EXTENDER_URL, gsmExtenderUrl),
                WriteRequest(Characteristic.REFRESH_ACTION, RefreshAction.GSM.code),
            )
        ).apply {
            addAll(viewModel.getStatusReadRequest())
            addAll(viewModel.getInternetReadRequests())
            addAll(viewModel.getLastOperationStateReadRequest())
        }
        BluetoothService.connectGatt(
            viewModel.btDevice,
            object : BluetoothCallback(bluetoothRequests) {
                override fun onSuccess() {
                    when (viewModel.lastOperationStatus.value) {
                        "gsm|ok" -> {
                            switchFragmentWithBackStack(
                                R.id.container,
                                ConfigurationSuccessfulFragment()
                            )
                        }
                        else -> internetConnectionFailed()
                    }
                }

                override fun onFailure() {
                    btConnectionFailed()
                }
            }
        )
    }

}
