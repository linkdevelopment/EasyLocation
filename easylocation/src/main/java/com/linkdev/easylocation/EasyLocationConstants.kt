package com.linkdev.easylocation

import com.google.android.gms.location.LocationRequest

internal object EasyLocationConstants {
    const val DEFAULT_MIN_DISTANCE: Float = 10F
    const val DEFAULT_PRIORITY: Int = LocationRequest.PRIORITY_HIGH_ACCURACY
    const val DEFAULT_INTERVAL: Long = 10000
    const val DEFAULT_FASTEST_INTERVAL: Long = 5000
    const val DEFAULT_MAX_LOCATION_REQUEST_TIME: Long = 50000
}
