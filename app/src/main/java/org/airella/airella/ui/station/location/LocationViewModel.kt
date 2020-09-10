package org.airella.airella.ui.station.location

import android.location.Location
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import org.airella.airella.data.bluetooth.BluetoothCallback
import org.airella.airella.data.bluetooth.BluetoothRequest
import org.airella.airella.data.bluetooth.WriteRequest
import org.airella.airella.ui.station.AbstractConfigViewModel
import org.airella.airella.utils.Config
import org.airella.airella.utils.Log
import java.util.*

class LocationViewModel : AbstractConfigViewModel() {

    var location: MutableLiveData<Location?> = MutableLiveData()

    fun saveLocation(fragment: Fragment, latitude: String, longitude: String) {
        Log.i("Save location start")
        val bluetoothRequests: Queue<BluetoothRequest> = LinkedList(
            listOf(
                WriteRequest(Config.LOCATION_LATITUDE_UUID, latitude),
                WriteRequest(Config.LOCATION_LONGITUDE_UUID, longitude),
                WriteRequest(Config.LOCATION_MANUALLY_UUID, "1"),
                WriteRequest(Config.REFRESH_ACTION_UUID, Config.LOCATION_ACTION)
            )
        )
        btDevice.connectGatt(
            fragment.requireContext(),
            false,
            object : BluetoothCallback(bluetoothRequests) {
                override fun onConnected() = setStatus("Connected")
                override fun onFailToConnect() = setStatus("Failed to connect")
                override fun onFailure() = setStatus("Saving location failed")
                override fun onCharacteristicWrite(characteristicUUID: UUID) {
                    setStatus("Saving location in progress...")
                }

                override fun onSuccess() {
                    setStatus("Success")
                    if (fragment.isAdded) {
                        fragment.parentFragmentManager.popBackStack()
                    }
                }
            })
    }
}