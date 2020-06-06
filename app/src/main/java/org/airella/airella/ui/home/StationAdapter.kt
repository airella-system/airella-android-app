package org.airella.airella.ui.home

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.station_list_item.view.*
import org.airella.airella.R
import org.airella.airella.data.model.sensor.Station
import org.airella.airella.utils.inflate

class StationAdapter : RecyclerView.Adapter<StationAdapter.StationHolder>() {

    private val stations: MutableList<Station> = arrayListOf()

    fun setStations(stations: List<Station>) {
        this.stations.clear()
        this.stations.addAll(stations)
        this.notifyDataSetChanged()
    }

    override fun getItemCount(): Int = stations.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StationHolder {
        val view = parent.inflate(R.layout.station_list_item, false)
        return StationHolder(view)
    }

    override fun onBindViewHolder(holder: StationHolder, position: Int) {
        holder.bind(stations[position])
    }


    class StationHolder(private var view: View) : RecyclerView.ViewHolder(view),
        View.OnClickListener {

        private lateinit var station: Station

        init {
            view.setOnClickListener(this)
        }

        fun bind(station: Station) {
            this.station = station

            view.station_name.text = station.name
            view.caiq_text.text = view.context.getString(R.string.x_caiq, station.caqi)
        }

        override fun onClick(v: View?) {
//            TODO("Not yet implemented")
        }

    }
}
