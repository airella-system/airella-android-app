package org.airella.airella.ui.home.station

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import org.airella.airella.R
import org.airella.airella.data.model.sensor.Station

class StationInfoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_station_info)

        val bundle = Bundle()
        bundle.putSerializable("station", intent.extras!!.getSerializable("station") as Station)

        val newFragment = StationSummaryFragment()
        newFragment.arguments = bundle
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, newFragment)
            .commitNow()
    }
}