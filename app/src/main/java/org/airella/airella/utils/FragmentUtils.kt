package org.airella.airella.utils

import androidx.annotation.StringRes
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import org.airella.airella.R
import org.airella.airella.ui.station.config.fail.ConfigurationFailedFragment
import org.airella.airella.ui.station.config.success.ConfigurationSuccessfulFragment

object FragmentUtils {

    fun Fragment.switchFragment(containerID: Int, newFragment: Fragment) {
        activity?.supportFragmentManager?.beginTransaction()
            ?.replace(containerID, newFragment)
            ?.commit()
    }

    fun Fragment.switchFragmentWithBackStack(
        containerID: Int,
        newFragment: Fragment,
        name: String? = null
    ) {
        activity?.supportFragmentManager?.beginTransaction()
            ?.replace(containerID, newFragment)
            ?.addToBackStack(name)
            ?.commit()
    }

    fun Fragment.clearBackStack() {
        val fm = requireActivity().supportFragmentManager
        for (i in 0 until fm.backStackEntryCount) {
            fm.popBackStack()
        }
    }

    fun Fragment.configSuccessful(@StringRes messageId: Int) {
        if (!isVisible) return
        val message = getString(messageId)
        Log.d(message)
        val configurationSuccessfulFragment = ConfigurationSuccessfulFragment()
        configurationSuccessfulFragment.arguments = bundleOf(Pair("success_text", message))
        switchFragmentWithBackStack(R.id.container, configurationSuccessfulFragment)
    }

    fun Fragment.configSuccessful() {
        configSuccessful(R.string.configuration_successful)
    }

    fun Fragment.configFailed(@StringRes errorMessageId: Int) {
        if (!isVisible) return
        val errorMessage = getString(errorMessageId)
        Log.w(errorMessage)
        val configurationFailedFragment = ConfigurationFailedFragment()
        configurationFailedFragment.arguments = bundleOf(Pair("fail_text", errorMessage))
        switchFragmentWithBackStack(R.id.container, configurationFailedFragment)
    }

    fun Fragment.configFailed() {
        configFailed(R.string.failed)
    }

    fun Fragment.internetConnectionFailed() {
        configFailed(R.string.station_internet_connection_failed)
    }

    fun Fragment.btConnectionFailed() {
        configFailed(R.string.bt_connection_failed)
    }
}