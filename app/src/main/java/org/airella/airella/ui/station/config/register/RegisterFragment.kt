package org.airella.airella.ui.station.config.register

import android.bluetooth.BluetoothDevice
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.fragment_register_station.*
import org.airella.airella.R
import org.airella.airella.data.service.AuthService
import org.airella.airella.utils.Config

class RegisterFragment : Fragment() {

    private lateinit var viewModel: RegisterViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this).get(RegisterViewModel::class.java)
        viewModel.btDevice = requireArguments().getParcelable("bt_device") as BluetoothDevice

        return inflater.inflate(R.layout.fragment_register_station, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        apiUrl.setText(Config.DEFAULT_API_URL)
        registrationToken.setText(AuthService.getUser().stationRegistrationToken)

        btn_continue.setText(R.string.action_save)

        btn_continue.setOnClickListener {
            viewModel.registerStation(
                this,
                stationName.text.toString(),
                apiUrl.text.toString()
            )
        }
    }

}
