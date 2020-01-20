package com.example.locationsample_kotlin_android.location

import android.content.Context
import android.location.LocationManager
import com.google.android.gms.location.LocationRequest

/**
 * Created by Mohammed Fareed on 30/5/2019.
 */
object LocationHelper {
    @JvmStatic // todo remove
    fun isLocationEnabled(context: Context): Boolean {
        val lm = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        var gpsEnabled = false
        try {
            gpsEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return gpsEnabled
    }
}