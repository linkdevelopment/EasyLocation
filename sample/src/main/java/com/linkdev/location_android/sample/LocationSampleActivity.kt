package com.linkdev.location_android.sample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.linkdev.location_android.R

class LocationSampleActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location_sample)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        if (savedInstanceState == null)
            supportFragmentManager.beginTransaction()
                    .replace(R.id.flFragmentContainer, SampleLocationFragment.newInstance(), SampleLocationFragment.TAG)
                    .commitNow()
    }

    override fun onPause() {
        super.onPause()
        println("life: onPause")
    }

    override fun onStop() {
        println("life: onStop")
        super.onStop()
    }

    override fun onDestroy() {
        println("life: onDestroy")
        super.onDestroy()
    }
}