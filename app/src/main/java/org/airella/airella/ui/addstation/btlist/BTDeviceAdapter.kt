package org.airella.airella.ui.addstation.btlist

import android.bluetooth.BluetoothDevice
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.bt_device_list_item.view.*
import org.airella.airella.R
import org.airella.airella.ui.addstation.wificonfig.WifiConfigFragment
import org.airella.airella.utils.inflate


class BTDeviceAdapter : RecyclerView.Adapter<BTDeviceAdapter.BtDeviceView>() {

    private val btDevices: MutableList<BluetoothDevice> = arrayListOf()

    fun setDevices(btDevices: List<BluetoothDevice>) {
        this.btDevices.clear()
        this.btDevices.addAll(btDevices)
        this.notifyDataSetChanged()
    }

    override fun getItemCount(): Int = btDevices.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BtDeviceView {
        val view = parent.inflate(R.layout.bt_device_list_item, false)
        return BtDeviceView(view)
    }

    override fun onBindViewHolder(holder: BtDeviceView, position: Int) {
        holder.bind(btDevices[position])
    }


    class BtDeviceView(private var view: View) : RecyclerView.ViewHolder(view),
        View.OnClickListener {

        private lateinit var btDevice: BluetoothDevice

        init {
            view.setOnClickListener(this)
        }

        fun bind(btDevice: BluetoothDevice) {
            this.btDevice = btDevice

            view.name.text = btDevice.name
            view.mac.text = btDevice.address
        }

        override fun onClick(v: View?) {
            v?.let {
                val wifiConfigFragment: Fragment = WifiConfigFragment()
                val transaction: FragmentTransaction =
                    (v.context as FragmentActivity).supportFragmentManager.beginTransaction()

                val bundle = Bundle()
                bundle.putParcelable("bt_device", btDevice)
                wifiConfigFragment.arguments = bundle

                transaction.replace(R.id.container, wifiConfigFragment)
                transaction.addToBackStack(null)
                transaction.commit()
            }
        }

    }
}

