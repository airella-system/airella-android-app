package org.airella.airella.ui.station

import android.bluetooth.BluetoothDevice
import android.widget.Toast
import androidx.lifecycle.ViewModel
import org.airella.airella.MyApplication
import org.airella.airella.data.bluetooth.BluetoothCallback
import org.airella.airella.data.bluetooth.BluetoothRequest
import org.airella.airella.utils.Log
import java.util.*

abstract class AbstractConfigViewModel : ViewModel() {

    lateinit var btDevice: BluetoothDevice

    private val toast by lazy { Toast.makeText(MyApplication.appContext, "", Toast.LENGTH_LONG) }

    protected fun setStatus(status: String) {
        Log.i(status)
        MyApplication.runOnUIThread {
            toast.setText(status)
            toast.show()
        }
    }

    open inner class DefaultConfigBluetoothCallback(bluetoothRequests: Queue<BluetoothRequest>) :
        BluetoothCallback(bluetoothRequests) {
        override fun onConnected() = setStatus("Connected")
        override fun onFailToConnect() = setStatus("Failed to connect")
        override fun onFailure() = setStatus("Saving failed")
        override fun onCharacteristicWrite(characteristicUUID: UUID) {
            setStatus("Saving in progress...")
        }

        override fun onSuccess() {
            setStatus("Success")
        }
    }
}