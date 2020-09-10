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

    fun Fragment.requestLocationIfDisabled(): Boolean {
        val locationPermission = checkLocationPermission(requireContext())
        if (!locationPermission) {
            switchFragment(R.id.container, LocationPermissionFragment(this))
        }
        return locationPermission
    }

    fun Fragment.requestBtIfDisabled(): Boolean {
        if (!requestLocationIfDisabled()) return true

        val btEnabled = checkBTEnabled(requireContext())
        if (!btEnabled) {
            switchFragment(R.id.container, BluetoothEnableFragment(this))
        }
        return !btEnabled
    }
}