package org.airella.airella.ui.station.wifilist

import android.annotation.SuppressLint
import android.app.Activity
import android.net.wifi.ScanResult
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_bt_device_list.view.*
import org.airella.airella.R
import org.airella.airella.utils.inflate


class WifiAdapter(val fragment: WifiListFragment, private val wifiList: MutableList<ScanResult>) :
    RecyclerView.Adapter<WifiAdapter.BtDeviceView>() {

    override fun getItemCount(): Int = wifiList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BtDeviceView {
        val view = parent.inflate(R.layout.item_wifi_list, false)
        return BtDeviceView(view)
    }

    override fun onBindViewHolder(holder: BtDeviceView, position: Int) {
        holder.bind(wifiList[position])
    }


    inner class BtDeviceView(private var view: View) : RecyclerView.ViewHolder(view),
        View.OnClickListener {

        private var wifi: ScanResult? = null

        init {
            view.setOnClickListener(this)
        }

        fun bind(wifi: ScanResult) {
            this.wifi = wifi

            view.name.text = wifi.SSID
        }

        @SuppressLint("InflateParams")
        override fun onClick(v: View) {
            val wifi = wifi ?: return

            val form = (view.context as Activity).layoutInflater.inflate(
                R.layout.view_device_wifi_password_config,
                null
            )
            val wifiPassword: EditText = form.findViewById(R.id.wifiPassword)

            AlertDialog.Builder(view.context)
                .setView(form)
                .setPositiveButton(R.string.action_save) { _, _ ->
                    fragment.saveWifiConfig(
                        wifi.SSID,
                        wifiPassword.text.toString()
                    )
                }
                .setNegativeButton(R.string.cancel, null)
                .create()
                .show()
        }

    }
}

