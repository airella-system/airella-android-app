package org.airella.airella.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_home.*
import org.airella.airella.R
import org.airella.airella.data.service.StationService

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

        homeViewModel.stationsList.observe(requireActivity(), Observer { stations ->
            adapter.setStations(stations)
        })

        StationService.getStations().subscribe { stations ->
            homeViewModel.stationsList.value = stations
        }

        add_sensor_fab.setOnClickListener {
            Snackbar.make(view, "Test fab", Snackbar.LENGTH_SHORT).show()
        }
    }

}