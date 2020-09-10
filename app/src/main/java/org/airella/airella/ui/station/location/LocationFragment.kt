package org.airella.airella.ui.station.location

import android.bluetooth.BluetoothDevice
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.android.synthetic.main.fragment_location.*
import org.airella.airella.R

class LocationFragment : Fragment() {

    private lateinit var viewModel: LocationViewModel

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        viewModel = ViewModelProvider(this).get(LocationViewModel::class.java)
        viewModel.btDevice = requireArguments().getParcelable("bt_device") as BluetoothDevice

        return inflater.inflate(R.layout.fragment_location, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        btn_continue.setText(R.string.action_save)

        viewModel.location.observe(viewLifecycleOwner, Observer {
            btn_continue.isEnabled = it != null
            val text = if (it != null) "${it.latitude}, ${it.longitude}" else "Can't get location"
            location_text.text = text
        })

        btn_continue.setOnClickListener {
            val location = viewModel.location.value ?: return@setOnClickListener
            viewModel.saveLocation(
                this,
                location.latitude.toString(),
                location.longitude.toString()
            )
        }

        refreshLocation()

        refresh_location.setOnClickListener {
            refreshLocation()
        }
    }

    private fun refreshLocation() {
        fusedLocationClient.lastLocation.addOnSuccessListener {
            viewModel.location.value = it
        }
    }

}
