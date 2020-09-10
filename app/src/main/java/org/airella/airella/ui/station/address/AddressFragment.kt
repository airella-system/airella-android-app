package org.airella.airella.ui.station.address

import android.bluetooth.BluetoothDevice
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.fragment_address.*
import org.airella.airella.R

class AddressFragment : Fragment() {

    private lateinit var viewModel: AddressViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this).get(AddressViewModel::class.java)
        viewModel.btDevice = requireArguments().getParcelable("bt_device") as BluetoothDevice

        return inflater.inflate(R.layout.fragment_address, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btn_continue.setText(R.string.action_save)

        btn_continue.setOnClickListener {
            viewModel.saveAddress(
                this,
                country.text.toString(),
                city.text.toString(),
                street.text.toString(),
                houseNo.text.toString()
            )
        }
    }

}
