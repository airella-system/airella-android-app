package org.airella.airella.utils

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity

object FragmentUtils {

    fun Fragment.switchFragmentNoBackStack(containerID: Int, newFragment: Fragment) {
        (context as FragmentActivity).supportFragmentManager.beginTransaction()
            .replace(containerID, newFragment)
//            .addToBackStack(null)
            .commit()
    }
}