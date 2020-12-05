package org.airella.airella.ui.station.config.wizard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import org.airella.airella.data.bluetooth.ReadRequest
import org.airella.airella.data.bluetooth.WriteRequest
import org.airella.airella.data.service.AuthService
import org.airella.airella.data.service.BluetoothService
import org.airella.airella.ui.OnBackPressed
import org.airella.airella.ui.station.config.ConfigViewModel
import org.airella.airella.ui.station.config.address.AddressViewModel
import org.airella.airella.ui.station.config.gsm.GsmViewModel
import org.airella.airella.ui.station.config.location.LocationViewModel
import org.airella.airella.ui.station.config.name.StationNameViewModel
import org.airella.airella.ui.station.config.wifi.WifiViewModel
import org.airella.airella.utils.FragmentUtils.btConnectionFailed
import org.airella.airella.utils.FragmentUtils.configFailed
import org.airella.airella.utils.FragmentUtils.configSuccessful
import org.airella.airella.utils.FragmentUtils.internetConnectionFailed
import org.airella.airella.utils.PermissionUtils
import java.util.*

class WizardFinishFragment : Fragment(), OnBackPressed {

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
            getStationNameSaveRequests()
        ).apply {
            if (wifiViewModel.wifiSSID.value != null) {
                addAll(getWifiSaveRequests())
            } else {
                addAll(getGsmSaveRequests())
            }
            addAll(getAddressSaveRequests())
            addAll(getLocationSaveRequests())
            addAll(getRegistrationSaveRequests())
            addAll(viewModel.getFullConfig())
        }
        BluetoothService.connectGatt(
            viewModel.btDevice,
            object : BluetoothCallback(bluetoothRequests) {
                override fun onSuccess() {
                    when {
                        viewModel.registered.value!!.isOK() -> configSuccessful(R.string.register_success)
                        else -> configFailed(R.string.registration_failed)
                    }
                }

                override fun onFailure() {
                    btConnectionFailed()
                }
            }
        )
    }

    private fun getStationNameSaveRequests(): List<BluetoothRequest> {
        return listOf(
            WriteRequest(Characteristic.STATION_NAME, stationNameViewModel.stationName.value!!),
            WriteRequest(Characteristic.REFRESH_ACTION, RefreshAction.NAME.code),
        )
    }

    private fun getWifiSaveRequests(): List<BluetoothRequest> {
        return listOf(
            WriteRequest(
                Characteristic.INTERNET_CONNECTION_TYPE,
                InternetConnectionType.WIFI.code
            ),
            WriteRequest(Characteristic.WIFI_SSID, wifiViewModel.wifiSSID.value!!),
            WriteRequest(
                Characteristic.WIFI_PASSWORD,
                wifiViewModel.wifiPassword.value!!
            ),
            WriteRequest(Characteristic.REFRESH_ACTION, RefreshAction.WIFI.code),
            ReadRequest(Characteristic.LAST_OPERATION_STATUS) {
                if (it == "wifi|error") {
                    internetConnectionFailed()
                }
            },
        )
    }

    private fun getGsmSaveRequests(): List<BluetoothRequest> {
        return listOf(
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
            ReadRequest(Characteristic.LAST_OPERATION_STATUS) {
                if (it == "gsm|error") {
                    internetConnectionFailed()
                }
            },
        )
    }

    private fun getAddressSaveRequests(): List<BluetoothRequest> {
        val address = addressViewModel.address.value!!
        return listOf(
            WriteRequest(Characteristic.STATION_COUNTRY, address.country),
            WriteRequest(Characteristic.STATION_CITY, address.city),
            WriteRequest(Characteristic.STATION_STREET, address.street),
            WriteRequest(Characteristic.STATION_HOUSE_NO, address.number),
            WriteRequest(Characteristic.REFRESH_ACTION, RefreshAction.ADDRESS.code),
        )
    }

    private fun getLocationSaveRequests(): List<BluetoothRequest> {
        val location = locationViewModel.location.value!!
        return listOf(
            WriteRequest(Characteristic.LOCATION_LATITUDE, location.latitude.toString()),
            WriteRequest(Characteristic.LOCATION_LONGITUDE, location.longitude.toString()),
            WriteRequest(Characteristic.LOCATION_MANUALLY, "1"),
            WriteRequest(Characteristic.REFRESH_ACTION, RefreshAction.LOCATION.code),
        )
    }

    private fun getRegistrationSaveRequests(): List<BluetoothRequest> {
        return listOf(
            WriteRequest(
                Characteristic.REGISTRATION_TOKEN,
                AuthService.getUser().stationRegistrationToken
            ),
            WriteRequest(Characteristic.API_URL, ApiManager.baseApiUrl),
            WriteRequest(Characteristic.REFRESH_ACTION, RefreshAction.REGISTER.code),
        )
    }


}