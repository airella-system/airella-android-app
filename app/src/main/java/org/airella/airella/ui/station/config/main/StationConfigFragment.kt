package org.airella.airella.ui.station.config.main

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.synthetic.main.fragment_station_config.*
import org.airella.airella.R
import org.airella.airella.config.InternetConnectionType
import org.airella.airella.ui.station.config.ConfigViewModel
import org.airella.airella.ui.station.config.address.AddressFragment
import org.airella.airella.ui.station.config.internet.InternetChooseFragment
import org.airella.airella.ui.station.config.location.LocationFragment
import org.airella.airella.ui.station.config.name.StationNameFragment
import org.airella.airella.ui.station.config.reset.ResetProgressFragment
import org.airella.airella.utils.FragmentUtils.switchFragmentWithBackStack
import org.airella.airella.utils.PermissionUtils

class StationConfigFragment : Fragment() {

    private val viewModel: ConfigViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        PermissionUtils.requestBtIfDisabled(this)

        return inflater.inflate(R.layout.fragment_station_config, container, false)
    }

    @SuppressLint("InflateParams", "SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.stationName.observe(viewLifecycleOwner, {
            station_name_view.text = it
        })

        viewModel.connectionType.observe(viewLifecycleOwner, {
            station_connection_type_view.text = it.toString()
            when (it) {
                InternetConnectionType.WIFI -> {
                    station_wifi_container.visibility = View.VISIBLE
                    station_gsm_container.visibility = View.GONE
                }
                InternetConnectionType.GSM -> {
                    station_wifi_container.visibility = View.GONE
                    station_gsm_container.visibility = View.VISIBLE
                }
                else -> {
                    station_wifi_container.visibility = View.GONE
                    station_gsm_container.visibility = View.GONE
                }
            }
        })

        viewModel.stationWifiSSID.observe(viewLifecycleOwner, {
            station_wifi_view.text = it
        })

        viewModel.gsmExtenderUrl.observe(viewLifecycleOwner, {
            station_gsm_extender_view.text = it
        })

        viewModel.gsmApn.observe(viewLifecycleOwner, {
            station_gsm_apn_view.text = "APN: $it"
        })

        viewModel.gsmUsername.observe(viewLifecycleOwner, {
            station_gsm_username_view.text = "Username: $it"
        })

        viewModel.gsmPassword.observe(viewLifecycleOwner, {
            station_gsm_password_view.text = "Password: $it"
        })


        fun setAddressStreetAndHouseNo() {
            station_address_view_1.text =
                "${viewModel.stationStreet.value ?: ""} ${viewModel.stationHouseNo.value ?: ""}"
        }

        viewModel.stationStreet.observe(viewLifecycleOwner, {
            setAddressStreetAndHouseNo()
        })

        viewModel.stationHouseNo.observe(viewLifecycleOwner, {
            setAddressStreetAndHouseNo()
        })

        fun setAddressCountryAndCity() {
            station_address_view_2.text =
                "${viewModel.stationCity.value ?: ""}, ${viewModel.stationCountry.value ?: ""}"
        }

        viewModel.stationCountry.observe(viewLifecycleOwner, {
            setAddressCountryAndCity()
        })

        viewModel.stationCity.observe(viewLifecycleOwner, {
            setAddressCountryAndCity()
        })

        fun setLocation() {
            station_location_view.text =
                "${viewModel.stationLatitude.value ?: ""}, ${viewModel.stationLongitude.value ?: ""}"
        }

        viewModel.stationLatitude.observe(viewLifecycleOwner, {
            setLocation()
        })

        viewModel.stationLongitude.observe(viewLifecycleOwner, {
            setLocation()
        })


        station_name_edit_button.setOnClickListener {
            goToConfigFragment(StationNameFragment())
        }

        station_wifi_edit_button.setOnClickListener {
            goToConfigFragment(InternetChooseFragment())
        }

        station_address_edit_button.setOnClickListener {
            goToConfigFragment(AddressFragment())
        }

        station_location_edit_button.setOnClickListener {
            goToConfigFragment(LocationFragment())
        }

        hard_reset.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(getString(R.string.clear_data))
                .setMessage("Are you sure you want to reset all configuration?")
                .setPositiveButton(android.R.string.yes) { _, _ ->
                    goToConfigFragment(ResetProgressFragment())
                }
                .setNegativeButton(R.string.cancel, null)
                .show()
        }
    }

    private fun goToConfigFragment(newFragment: Fragment) {
        switchFragmentWithBackStack(R.id.container, newFragment, "config")
    }
}

