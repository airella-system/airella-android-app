package org.airella.airella.ui.station.config.location

import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothDevice
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.synthetic.main.fragment_location.*
import org.airella.airella.R
import org.airella.airella.data.model.common.Location
import org.airella.airella.utils.PermissionUtils

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

        location_manually.setOnClickListener {

            val form = (view.context as Activity).layoutInflater.inflate(
                R.layout.view_device_location_config,
                null
            )
            val latitude: EditText = form.findViewById(R.id.latitude)
            val longitude: EditText = form.findViewById(R.id.longitude)

            val alert = MaterialAlertDialogBuilder(view.context)
                .setTitle("Set location manually:")
                .setView(form)
                .setPositiveButton(R.string.action_save) { _, _ ->
                    val lat = latitude.text.toString().toDoubleOrNull()
                    val long = longitude.text.toString().toDoubleOrNull()
                    if (lat != null && long != null) {
                        viewModel.location.value = Location.createWithValidation(lat, long)
                    }
                }
                .setNegativeButton(R.string.cancel, null)
                .create()

            alert.show()
        }
    }

    @SuppressLint("MissingPermission")
    private fun refreshLocation() {
        if (PermissionUtils.requestLocationIfDisabled(this)) {
            fusedLocationClient.lastLocation.addOnSuccessListener {
                viewModel.location.value = if (it != null) Location(it) else null
            }
        } else {
            viewModel.location.value = null
        }
    }

}
