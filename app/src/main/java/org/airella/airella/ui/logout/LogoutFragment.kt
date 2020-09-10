package org.airella.airella.ui.logout

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import org.airella.airella.data.service.AuthService
import org.airella.airella.utils.AuthUtils

class LogoutFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        AuthService.logout()
        AuthUtils.goToLoginPage(requireContext())
        return null
    }
}