package org.airella.airella.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_home.*
import org.airella.airella.R
import org.airella.airella.data.service.UserService
import org.airella.airella.ui.station.btlist.BtListActivity
import org.airella.airella.utils.Log

class HomeFragment : Fragment() {

    private val homeViewModel: HomeViewModel by viewModels()

    private lateinit var adapter: StationAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        stations_list.layoutManager = LinearLayoutManager(requireActivity())

        adapter = StationAdapter()
        stations_list.adapter = adapter

        homeViewModel.stationsList.observe(viewLifecycleOwner, { stations ->
            adapter.setStations(stations)
        })

        add_sensor_fab.setOnClickListener {
            val intent = Intent(requireContext(), BtListActivity::class.java)
            startActivity(intent)
        }

        station_list_refresh.setOnClickListener {
            getStations()
        }
    }

    override fun onStart() {
        super.onStart()
        getStations()
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
            homeViewModel.stationsList.value = listOf()
            station_list_info.setText(R.string.enable_internet_to_show_your_stations)
            station_list_info.visibility = View.VISIBLE
            station_list_refresh.visibility = View.VISIBLE
        })
    }

}