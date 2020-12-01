package org.airella.airella.ui.station.config.wizard

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
import org.airella.airella.config.InternetConnectionType
import org.airella.airella.config.RefreshAction
import org.airella.airella.data.api.ApiManager
import org.airella.airella.data.bluetooth.BluetoothCallback
import org.airella.airella.data.bluetooth.BluetoothRequest
import org.airella.airella.data.bluetooth.WriteRequest
import org.airella.airella.data.service.AuthService
import org.airella.airella.ui.station.config.ConfigViewModel
import org.airella.airella.ui.station.config.address.AddressViewModel
import org.airella.airella.ui.station.config.fail.ConfigurationFailedFragment
import org.airella.airella.ui.station.config.gsm.GsmViewModel
import org.airella.airella.ui.station.config.location.LocationViewModel
import org.airella.airella.ui.station.config.name.StationNameViewModel
import org.airella.airella.ui.station.config.success.ConfigurationSuccessfulFragment
import org.airella.airella.ui.station.config.wifi.WifiViewModel
import org.airella.airella.utils.FragmentUtils.switchFragmentWithBackStack
import org.airella.airella.utils.Log
import org.airella.airella.utils.PermissionUtils
import java.util.*

class WizardFinishFragment : Fragment() {

    private val viewModel: ConfigViewModel by activityViewModels()

    private val stationNameViewModel: StationNameViewModel by activityViewModels()

    private val wifiViewModel: WifiViewModel by activityViewModels()

    private val gsmViewModel: GsmViewModel by activityViewModels()

    private val addressViewModel: AddressViewModel by activityViewModels()

    private val locationViewModel: LocationViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        PermissionUtils.requestBtIfDisabled(this)
        return inflater.inflate(R.layout.fragment_configuration_progress, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configuration_text.setText(R.string.registration_in_progress)
        saveConfig()
    }

    private fun saveConfig() {
        val bluetoothRequests: Queue<BluetoothRequest> = LinkedList<BluetoothRequest>(
            listOf(
                WriteRequest(Characteristic.STATION_NAME, stationNameViewModel.stationName.value!!),
                WriteRequest(Characteristic.REFRESH_ACTION, RefreshAction.NAME.code),
                WriteRequest(
                    Characteristic.INTERNET_CONNECTION_TYPE,
                    InternetConnectionType.WIFI.code
                )
            )
        ).apply {
            if (wifiViewModel.wifiSSID.value != null) {
                addAll(
                    listOf(
                        WriteRequest(Characteristic.WIFI_SSID, wifiViewModel.wifiSSID.value!!),
                        WriteRequest(
                            Characteristic.WIFI_PASSWORD,
                            wifiViewModel.wifiPassword.value!!
                        ),
                        WriteRequest(Characteristic.REFRESH_ACTION, RefreshAction.WIFI.code),
                    )
                )
            } else {
                addAll(
                    listOf(
                        WriteRequest(
                            Characteristic.INTERNET_CONNECTION_TYPE,
                            InternetConnectionType.GSM.code
                        ),
                        WriteRequest(
                            Characteristic.GSM_CONFIG,
                            """"${gsmViewModel.apn.value!!}","${gsmViewModel.username.value!!}","${gsmViewModel.password.value!!}""""
                        ),
                        WriteRequest(
                            Characteristic.GSM_EXTENDER_URL,
                            gsmViewModel.extenderUrl.value!!
                        ),
                        WriteRequest(Characteristic.REFRESH_ACTION, RefreshAction.GSM.code),
                    )
                )
            }
            val address = addressViewModel.address.value!!
            val location = locationViewModel.location.value!!
            addAll(
                listOf(
                    WriteRequest(Characteristic.STATION_COUNTRY, address.country),
                    WriteRequest(Characteristic.STATION_CITY, address.city),
                    WriteRequest(Characteristic.STATION_STREET, address.street),
                    WriteRequest(Characteristic.STATION_HOUSE_NO, address.number),
                    WriteRequest(Characteristic.REFRESH_ACTION, RefreshAction.ADDRESS.code),
                    WriteRequest(Characteristic.LOCATION_LATITUDE, location.latitude.toString()),
                    WriteRequest(Characteristic.LOCATION_LONGITUDE, location.longitude.toString()),
                    WriteRequest(Characteristic.LOCATION_MANUALLY, "1"),
                    WriteRequest(Characteristic.REFRESH_ACTION, RefreshAction.LOCATION.code),
                    WriteRequest(
                        Characteristic.REGISTRATION_TOKEN,
                        AuthService.getUser().stationRegistrationToken
                    ),
                    WriteRequest(Characteristic.API_URL, ApiManager.baseApiUrl),
                    WriteRequest(Characteristic.REFRESH_ACTION, RefreshAction.REGISTER.code)
                )
            )
            addAll(viewModel.getFullConfig())
        }
        viewModel.btDevice.connectGatt(
            context,
            false,
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
            }
        )
    }

    private fun configFailed() {
        Log.d("Failed")
        switchFragmentWithBackStack(
            R.id.container,
            ConfigurationFailedFragment()
        )
    }
}