package org.airella.airella.ui.station.config

import android.bluetooth.BluetoothDevice
import android.content.Context
import org.airella.airella.data.bluetooth.BluetoothCallback
import org.airella.airella.data.bluetooth.BluetoothRequest
import org.airella.airella.data.bluetooth.WriteRequest
import org.airella.airella.ui.station.AbstractConfigViewModel
import org.airella.airella.utils.Config
import org.airella.airella.utils.Log
import java.util.*

class StationConfigViewModel : AbstractConfigViewModel() {

    fun isBonded() = btDevice.bondState == BluetoothDevice.BOND_BONDED

    fun hardResetDevice(context: Context) {
        Log.i("Hard reset started")
        setStatus("Connecting")
        val bluetoothRequests: Queue<BluetoothRequest> = LinkedList(
            listOf(
                WriteRequest(Config.CLEAR_DATA_UUID, "")
            )
        )

        btDevice.connectGatt(context, false, object : BluetoothCallback(bluetoothRequests) {
            override fun onFailToConnect() = setStatus("Failed to connect")
            override fun onSuccess() = setStatus("Hard reset successful")
            override fun onFailure() = setStatus("Hard reset failed")
        })
    }

}


