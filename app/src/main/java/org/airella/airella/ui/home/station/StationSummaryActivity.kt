package org.airella.airella.ui.home.station

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.activity_station_summary.*
import org.airella.airella.R
import org.airella.airella.data.model.sensor.Station
import org.airella.airella.data.service.StationService

class StationSummaryActivity : AppCompatActivity() {

    private lateinit var viewModel: StationSummaryViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_station_summary)

        viewModel = ViewModelProvider(this).get(StationSummaryViewModel::class.java)
        viewModel.station = intent.extras!!.getSerializable("station") as Station

        name.text = viewModel.station.name
        aqi.text = viewModel.station.aqi.toString()
        address.text = viewModel.station.address.toString()

        station_connect_button.setOnClickListener {
            Toast.makeText(this, "Connection to a station", Toast.LENGTH_SHORT).show()
        }

        remove_station_button.setOnClickListener {
            Toast.makeText(this, "Removing a station", Toast.LENGTH_SHORT).show()
            StationService.removeStation(viewModel.station.id).subscribe({
                Toast.makeText(this, "Removed a station", Toast.LENGTH_LONG).show()
                finish()
            }, {
                Toast.makeText(this, "Failed to remove a station", Toast.LENGTH_LONG).show()
            })
        }
    }
}