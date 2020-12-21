package org.airella.airella.ui.station.config.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.fragment_station.*
import org.airella.airella.R
import org.airella.airella.data.bluetooth.BluetoothCallback
import org.airella.airella.data.service.BluetoothService
import org.airella.airella.ui.station.config.ConfigViewModel
import org.airella.airella.utils.PermissionUtils
import java.util.*

class StationMainFragment : Fragment() {

    private val viewModel: ConfigViewModel by activityViewModels()

    private lateinit var refreshStatusTimer: Timer

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        PermissionUtils.requestBtIfDisabled(this)

        return inflater.inflate(R.layout.fragment_station, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewPager.adapter = StationFragmentAdapter()
        TabLayoutMediator(tabLayout, viewPager) { tab, pos ->
            tab.text = when (pos) {
                0 -> getString(R.string.configure)
                1 -> getString(R.string.status)
                else -> ""
            }
        }.attach()
    }


    inner class StationFragmentAdapter : FragmentStateAdapter(this) {

        override fun getItemCount(): Int = 2

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> StationConfigFragment()
                1 -> StationStatusFragment()
                else -> throw IllegalArgumentException()
            }
        }
    }

    override fun onResume() {
        super.onResume()

        val timerTask = object : TimerTask() {
            override fun run() {
                BluetoothService.connectGatt(
                    viewModel.btDevice,
                    BluetoothCallback(viewModel.getStatusReadRequest())
                )
            }
        }

        refreshStatusTimer = Timer()
        refreshStatusTimer.schedule(timerTask, 0L, 5000L)
    }

    override fun onPause() {
        super.onPause()

        refreshStatusTimer.cancel()
        refreshStatusTimer.purge()
    }

}

