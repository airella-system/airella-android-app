package org.airella.airella.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_home.*
import org.airella.airella.R
import org.airella.airella.data.model.sensor.Station
import org.airella.airella.data.service.UserService
import org.airella.airella.ui.station.AddSensorActivity
import org.airella.airella.utils.Log

class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel

    private lateinit var adapter: StationAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)

        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        stations_list.layoutManager = LinearLayoutManager(requireActivity())

        adapter = StationAdapter()
        stations_list.adapter = adapter

        homeViewModel.stationsList.observe(viewLifecycleOwner, Observer { stations ->
            adapter.setStations(stations)
        })

        val stations = listOf(
            Station("1", "test", null, null, 9.0, listOf()),
            Station("1", "test", null, null, 11.0, listOf())
        )
        homeViewModel.stationsList.value = stations

//        getStations()

        add_sensor_fab.setOnClickListener {
            val intent = Intent(requireContext(), AddSensorActivity::class.java)
            startActivity(intent)
        }

        station_list_refresh.setOnClickListener {
            getStations()
        }
    }


    private fun getStations() {
        UserService.getUserStations().subscribe({ stations ->
            Log.i("Loaded user stations")
            station_list_info.visibility = View.GONE
            station_list_refresh.visibility = View.GONE
            homeViewModel.stationsList.value = stations
            if (stations.isNullOrEmpty()) {
                station_list_info.setText(R.string.register_stations_to_see_them)
                station_list_info.visibility = View.VISIBLE
            }
        }, {
            Log.e(it.toString())
            Log.e(it.message ?: getString(R.string.unexpected_error))
            station_list_info.setText(R.string.enable_internet_to_show_your_stations)
            station_list_info.visibility = View.VISIBLE
            station_list_refresh.visibility = View.VISIBLE
        })
    }

}