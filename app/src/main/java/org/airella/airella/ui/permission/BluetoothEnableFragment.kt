package org.airella.airella.ui.permission

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_bt_enable.*
import org.airella.airella.R
import org.airella.airella.config.Config.BT_ENABLE_CONST
import org.airella.airella.utils.FragmentUtils.switchFragment

class BluetoothEnableFragment(private val nextFragment: Fragment) : Fragment() {

    private val bluetoothAdapter: BluetoothAdapter? by lazy { BluetoothAdapter.getDefaultAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_bt_enable, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        enable_bt_button.setOnClickListener {
            checkBt()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == BT_ENABLE_CONST) {
            when (resultCode) {
                Activity.RESULT_OK -> {
                    switchFragment(R.id.container, nextFragment)
                }
            }
        }
    }


    private fun checkBt() {
        if (bluetoothAdapter != null && bluetoothAdapter!!.isEnabled) {
            switchFragment(R.id.container, nextFragment)
        } else {
            startActivityForResult(
                Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE),
                BT_ENABLE_CONST
            )
        }
    }
}