package com.example.locationsample_kotlin_android.location

import com.google.android.gms.location.LocationRequest

object Constants {
    const val DEFAULT_MIN_DISTANCE: Float = 10F
    const val DEFAULT_PRIORITY: Int = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
    const val DEFAULT_MAX_WAIT_TIME: Long = 50000
    const val DEFAULT_FASTEST_INTERVAL: Long = 1000
    const val DEFAULT_MAX_LOCATION_REQUEST_TIME: Long = 15000
}
