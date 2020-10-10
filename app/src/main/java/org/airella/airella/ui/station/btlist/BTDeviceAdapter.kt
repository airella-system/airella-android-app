package org.airella.airella.ui.station.btlist

import android.bluetooth.BluetoothDevice
import android.content.Intent
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_bt_device_list.view.*
import org.airella.airella.R
import org.airella.airella.ui.station.config.StationConfigActivity
import org.airella.airella.utils.inflate


class BTDeviceAdapter(private val btDevices: MutableList<BluetoothDevice>) :
    RecyclerView.Adapter<BTDeviceAdapter.BtDeviceView>() {

    override fun getItemCount(): Int = btDevices.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BtDeviceView {
        val view = parent.inflate(R.layout.item_bt_device_list, false)
        return BtDeviceView(view)
    }

    override fun onBindViewHolder(holder: BtDeviceView, position: Int) {
        holder.bind(btDevices[position])
    }


    class BtDeviceView(private var view: View) : RecyclerView.ViewHolder(view),
        View.OnClickListener {

        private var btDevice: BluetoothDevice? = null

        init {
            view.setOnClickListener(this)
        }

        fun bind(btDevice: BluetoothDevice) {
            this.btDevice = btDevice

            view.name.text = btDevice.name
            view.mac.text = btDevice.address
        }

        override fun onClick(v: View) {
            if (btDevice == null) {
                return
            }
            val intent = Intent(v.context, StationConfigActivity::class.java)
            intent.putExtra("bt_device", btDevice)
            ContextCompat.startActivity(v.context, intent, null)
        }

    }
}

