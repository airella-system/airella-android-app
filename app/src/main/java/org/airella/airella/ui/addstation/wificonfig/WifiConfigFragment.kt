package org.airella.airella.ui.addstation.wificonfig

import android.bluetooth.BluetoothDevice
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.fragment_wifi_config.*
import org.airella.airella.R
import org.airella.airella.utils.afterTextChanged

class WifiConfigFragment : Fragment() {

    private lateinit var viewModel: WifiConfigViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this).get(WifiConfigViewModel::class.java)

        viewModel.btDevice = requireArguments().getParcelable("bt_device") as BluetoothDevice

        return inflater.inflate(R.layout.fragment_wifi_config, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        wifiSSID.afterTextChanged {
            viewModel.wifiSSIDChanged(wifiSSID.text.toString())
        }

        wifiPassword.afterTextChanged {
            viewModel.wifiPasswordChanged(wifiPassword.text.toString())
        }

        save.setOnClickListener {
            viewModel.saveConfig(requireContext())
        }

        viewModel.statusToast.observe(requireActivity(), Observer {
            Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
        })
    }

}

