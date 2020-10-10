package org.airella.airella.ui.station.btlist

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import org.airella.airella.R

class BtListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_container)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, BTListFragment())
                .commitNow()
        }
    }
}
