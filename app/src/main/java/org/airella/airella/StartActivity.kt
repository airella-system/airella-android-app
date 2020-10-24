package org.airella.airella

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
    var launched = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        PreferencesService.init(applicationContext)

        val motionLayout: MotionLayout = findViewById(R.id.motion_layout)
        val context = applicationContext;
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
                    if (AuthService.isUserLogged()) {
                        Log.i("User logged")
                        val intent = Intent(context, MainActivity::class.java)
                        intent.flags =
                            Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    } else {
                        Log.i("User not logged")
                        val intent = Intent(context, LoginActivity::class.java)
                        intent.flags =
                            Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

                    }
                }
            }

            override fun onTransitionStarted(p0: MotionLayout?, p1: Int, p2: Int) {}

            override fun onTransitionTrigger(p0: MotionLayout?, p1: Int, p2: Boolean, p3: Float) {}
        })
    }

    override fun onResume() {
        super.onResume()
    }
}
