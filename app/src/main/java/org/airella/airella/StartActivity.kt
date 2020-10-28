package org.airella.airella

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.drawerlayout.widget.DrawerLayout
import org.airella.airella.data.service.AuthService
import org.airella.airella.data.service.PreferencesService
import org.airella.airella.ui.login.LoginActivity
import org.airella.airella.utils.Log

class StartActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        PreferencesService.init(applicationContext)

        val motionLayout: MotionLayout = findViewById(R.id.motion_layout)
        var launched = false
        motionLayout.setTransitionListener(object : MotionLayout.TransitionListener {
            override fun onTransitionCompleted(p0: MotionLayout?, p1: Int) {

            }

            override fun onTransitionChange(
                p0: MotionLayout?,
                p1: Int,
                p2: Int,
                animationPercent: Float
            ) {
                if (animationPercent > 0.8 && !launched) {
                    launched = true
                    Log.i(if (AuthService.isUserLogged()) "User logged" else "User not logged")
                    val intent = Intent(
                        this@StartActivity,
                        if (AuthService.isUserLogged()) MainActivity::class.java else LoginActivity::class.java
                    )
                    intent.flags =
                        Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    val options = ActivityOptions
                        .makeCustomAnimation(
                            this@StartActivity,
                            R.anim.fade_in,
                            R.anim.fade_out
                        )
                        .toBundle()
                    startActivity(intent, options)
                }
            }

            override fun onTransitionStarted(p0: MotionLayout?, p1: Int, p2: Int) {}

            override fun onTransitionTrigger(p0: MotionLayout?, p1: Int, p2: Boolean, p3: Float) {}
        })
    }
}
