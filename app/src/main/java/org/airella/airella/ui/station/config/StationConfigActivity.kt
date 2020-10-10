package org.airella.airella.ui.station.config;

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import org.airella.airella.R
import org.airella.airella.ui.station.config.list.StationConfigFragment

class StationConfigActivity : AppCompatActivity() {

    private val viewModel: ConfigViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_container)

        viewModel.btDevice = intent.extras!!.getParcelable("bt_device")!!

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, StationConfigFragment())
                .commitNow()
        }
    }
}