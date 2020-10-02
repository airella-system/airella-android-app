package org.airella.airella.ui.station.config.wifilist

import android.annotation.SuppressLint
import android.app.Activity
import android.net.wifi.ScanResult
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
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

            MaterialAlertDialogBuilder(view.context)
                .setTitle("Type WIFI password:")
                .setView(form)
                .setPositiveButton(R.string.action_save) { _, _ ->
                    fragment.saveWifiConfig(
                        wifi.SSID,
                        wifiPassword.text.toString()
                    )
                }
                .setNegativeButton(R.string.cancel, null)
                .show()
        }

    }
}

