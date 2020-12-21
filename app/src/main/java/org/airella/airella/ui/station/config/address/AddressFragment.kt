package org.airella.airella.ui.station.config.address

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import kotlinx.android.synthetic.main.fragment_address.*
import org.airella.airella.R
import org.airella.airella.data.model.common.Address
import org.airella.airella.ui.station.config.ConfigViewModel
import org.airella.airella.ui.station.config.location.LocationFragment
import org.airella.airella.utils.FragmentUtils.switchFragmentWithBackStack

class AddressFragment : Fragment() {

    private val configViewModel: ConfigViewModel by activityViewModels()

    private val addressViewModel: AddressViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_address, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        country.setText(configViewModel.stationCountry.value)
        city.setText(configViewModel.stationCity.value)
        street.setText(configViewModel.stationStreet.value)
        houseNo.setText(configViewModel.stationHouseNo.value)

        btn_continue.setText(if (configViewModel.isWizard()) R.string.action_continue else R.string.action_save)

        btn_continue.setOnClickListener {
            addressViewModel.address.value = Address(
                country.text.toString(),
                city.text.toString(),
                street.text.toString(),
                houseNo.text.toString()
            )
            if (configViewModel.isWizard()) {
                switchFragmentWithBackStack(R.id.container, LocationFragment())
            } else {
                switchFragmentWithBackStack(R.id.container, AddressProgressFragment())
            }
        }
    }

}
