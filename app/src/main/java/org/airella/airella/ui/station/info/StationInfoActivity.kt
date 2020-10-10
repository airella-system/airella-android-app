package org.airella.airella.ui.station.info

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import org.airella.airella.R
import org.airella.airella.data.model.sensor.Station

class StationInfoActivity : AppCompatActivity() {

    private val viewModel: StationInfoViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_container)

        viewModel.station = intent.extras!!.getSerializable("station") as Station

        supportFragmentManager.beginTransaction()
            .replace(R.id.container, StationSummaryFragment())
            .commitNow()
    }
}