package org.airella.airella.utils

import androidx.fragment.app.Fragment

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
}