package org.airella.airella.utils

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity

object FragmentUtils {

    fun Fragment.switchFragment(containerID: Int, newFragment: Fragment) {
        (context as FragmentActivity).supportFragmentManager.beginTransaction()
            .replace(containerID, newFragment)
            .commit()
    }

    fun Fragment.switchFragmentWithBackStack(containerID: Int, newFragment: Fragment) {
        (context as FragmentActivity).supportFragmentManager.beginTransaction()
            .replace(containerID, newFragment)
            .addToBackStack(null)
            .commit()
    }
}