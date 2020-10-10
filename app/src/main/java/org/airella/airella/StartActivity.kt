package org.airella.airella

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import org.airella.airella.data.service.AuthService
import org.airella.airella.data.service.PreferencesService
import org.airella.airella.ui.login.LoginActivity
import org.airella.airella.utils.Log

class StartActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        PreferencesService.init(applicationContext)

        setContentView(R.layout.activity_start)
    }

    override fun onResume() {
        super.onResume()
        if (AuthService.isUserLogged()) {
            Log.i("User logged")
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        } else {
            Log.i("User not logged")
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }
    }
}
