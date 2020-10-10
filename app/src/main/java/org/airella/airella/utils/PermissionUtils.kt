package org.airella.airella.utils

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import org.airella.airella.R
import org.airella.airella.ui.permission.BluetoothEnableFragment
import org.airella.airella.ui.permission.LocationPermissionFragment
import org.airella.airella.utils.FragmentUtils.switchFragment

object PermissionUtils {

    fun checkLocationPermission(context: Context): Boolean {
        val locationPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        return locationPermission == PackageManager.PERMISSION_GRANTED
    }

    fun checkBTEnabled(context: Context): Boolean {
        val bluetoothAdapter: BluetoothAdapter? by lazy(LazyThreadSafetyMode.NONE) {
            val bluetoothManager = context
                .getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
            bluetoothManager.adapter
        }
        return bluetoothAdapter != null && bluetoothAdapter!!.isEnabled
    }

    fun requestLocationIfDisabled(fragment: Fragment): Boolean {
        val locationPermission = checkLocationPermission(fragment.requireContext())
        if (!locationPermission) {
            fragment.switchFragment(R.id.container, LocationPermissionFragment(fragment))
        }
        return locationPermission
    }

    fun requestBtIfDisabled(fragment: Fragment): Boolean {
        if (!requestLocationIfDisabled(fragment)) return true

        val btEnabled = checkBTEnabled(fragment.requireContext())
        if (!btEnabled) {
            fragment.switchFragment(R.id.container, BluetoothEnableFragment(fragment))
        }
        return !btEnabled
    }
}