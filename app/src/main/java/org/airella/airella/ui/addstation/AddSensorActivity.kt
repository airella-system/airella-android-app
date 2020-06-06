package org.airella.airella.ui.addstation

import android.bluetooth.BluetoothDevice
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import org.airella.airella.R
import org.airella.airella.ui.addstation.btlist.BTListFragment

class AddSensorActivity : AppCompatActivity() {

    lateinit var selectedBtDevice: BluetoothDevice

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_sensor)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, BTListFragment())
                .commitNow()
        }
    }
}
