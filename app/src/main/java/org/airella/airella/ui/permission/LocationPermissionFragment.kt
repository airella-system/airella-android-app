package org.airella.airella.ui.permission

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_location_enable.*
import org.airella.airella.R
import org.airella.airella.utils.Config.REQUEST_FINE_LOCATION_CONST
import org.airella.airella.utils.FragmentUtils.switchFragmentNoBackStack
import org.airella.airella.utils.PermissionUtils

class LocationPermissionFragment(private val nextFragment: Fragment) : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_location_enable, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        enable_location_button.setOnClickListener {
            if (checkAndAskForLocationPermission(this)) {
                switchFragmentNoBackStack(R.id.container, nextFragment)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            REQUEST_FINE_LOCATION_CONST -> {
                if (grantResults.isNotEmpty()
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                ) {
                    if (PermissionUtils.checkLocationPermission(requireContext())) {
                        switchFragmentNoBackStack(R.id.container, nextFragment)
                    }
                } else {
                    // user checked Never ask again
                    if (!shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                        enable_location_button.isEnabled = false
                    }
                }
            }
        }
    }

    private fun checkAndAskForLocationPermission(fragment: Fragment): Boolean {
        val locationEnabled = PermissionUtils.checkLocationPermission(fragment.requireContext())
        if (!locationEnabled) {
            fragment.requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_FINE_LOCATION_CONST
            )
        }
        return locationEnabled
    }

}