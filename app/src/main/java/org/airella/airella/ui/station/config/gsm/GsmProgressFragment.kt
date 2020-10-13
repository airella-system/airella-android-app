package org.airella.airella.ui.station.config.gsm

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import org.airella.airella.MyApplication.Companion.runOnUIThread
import org.airella.airella.MyApplication.Companion.setStatus
import org.airella.airella.R
import org.airella.airella.data.bluetooth.BluetoothCallback
import org.airella.airella.data.bluetooth.BluetoothRequest
import org.airella.airella.ui.station.config.ConfigViewModel
import org.airella.airella.ui.station.config.success.ConfigurationSuccessfulFragment
import org.airella.airella.utils.FragmentUtils.switchFragmentWithBackStack
import org.airella.airella.config.InternetConnectionType
import org.airella.airella.utils.Log
import java.util.*

class GsmProgressFragment : Fragment() {

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
        val gmsUsername = gsmViewModel.apn.value!!
        val gsmPassword = gsmViewModel.apn.value!!
        val gsmExtenderUrl = gsmViewModel.apn.value!!
        saveGsm(apn, gmsUsername, gsmPassword, gsmExtenderUrl)
    }

    private fun saveGsm(apn: String, gsmUsername: String, gsmPassword: String, gsmExtenderUrl: String) {
        Log.i("Save gsm config start")
        val bluetoothRequests: Queue<BluetoothRequest> = LinkedList(
            listOf(
//                WriteRequest(, Config.InternetConnectionType.GSM.value),
//                WriteRequest(, apn),
//                WriteRequest(, gmsUsername),
//                WriteRequest(, gsmPassword),
//                WriteRequest(, gsmExtenderUrl),
//                WriteRequest(Config.REFRESH_ACTION_UUID, Config.LOCATION_ACTION)
            )
        )
        viewModel.btDevice.connectGatt(
            context,
            false,
            object : BluetoothCallback(bluetoothRequests) {
                override fun onSuccess() {
                    runOnUIThread {
                        setStatus("Success")
                        viewModel.connectionType.value = InternetConnectionType.GSM
                        viewModel.gsmApn.value = apn
                        viewModel.gsmUsername.value = gsmUsername
                        viewModel.gsmPassword.value = gsmPassword
                        viewModel.gsmExtenderUrl.value = gsmExtenderUrl
                        switchFragmentWithBackStack(
                            R.id.container,
                            ConfigurationSuccessfulFragment()
                        )
                    }
                }
            })
    }

}
