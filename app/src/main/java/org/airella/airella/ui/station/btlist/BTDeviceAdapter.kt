package org.airella.airella.ui.station.btlist

import android.bluetooth.BluetoothDevice
import android.content.Intent
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_bt_device_list.view.*
import org.airella.airella.MyApplication.Companion.createToast
import org.airella.airella.MyApplication.Companion.runOnUiThread
import org.airella.airella.R
import org.airella.airella.data.bluetooth.BluetoothCallback
import org.airella.airella.ui.station.config.StationActivity
import org.airella.airella.utils.inflate
import java.util.*
import kotlin.concurrent.schedule


class BTDeviceAdapter(private val btDevices: MutableList<BluetoothDevice>) :
    RecyclerView.Adapter<BTDeviceAdapter.BtDeviceView>() {

    var allowConnecting = true

    override fun getItemCount(): Int = btDevices.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BtDeviceView {
        val view = parent.inflate(R.layout.item_bt_device_list, false)
        return BtDeviceView(view)
    }

    override fun onBindViewHolder(holder: BtDeviceView, position: Int) {
        holder.bind(btDevices[position])
    }


    inner class BtDeviceView(private var view: View) : RecyclerView.ViewHolder(view),
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
            if (btDevice == null || !allowConnecting) {
                return
            }
            allowConnecting = false
            val btDevice = btDevice!!

            createToast("Connecting to device")

            val timeout = Timer().schedule(5_000L) {
                createToast("Can't connect to device")
                allowConnecting = true
            }


            btDevice.connectGatt(v.context, false, object : BluetoothCallback(LinkedList()) {
                override fun onSuccess() {
                    timeout.cancel()
                    allowConnecting = true
                    runOnUiThread {
                        val intent = Intent(v.context, StationActivity::class.java)
                        intent.putExtra("bt_device", btDevice)
                        ContextCompat.startActivity(v.context, intent, null)
                    }
                }

                override fun onFailToConnect() {
                    allowConnecting = true
                    timeout.cancel()
                    createToast("This device isn't Airella station")
                }

                override fun onFailure() {
                    allowConnecting = true
                    timeout.cancel()
                    createToast("This device isn't Airella station")
                }
            })
        }

    }
}

