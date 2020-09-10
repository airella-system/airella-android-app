package org.airella.airella.ui.station

import android.bluetooth.BluetoothDevice
import android.widget.Toast
import androidx.lifecycle.ViewModel
import org.airella.airella.MyApplication
import org.airella.airella.utils.Log

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
}