package org.airella.airella.ui.station

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import org.airella.airella.R
import org.airella.airella.ui.station.btlist.BTListFragment

class AddStationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_station)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, BTListFragment())
                .commitNow()
        }
    }
}
