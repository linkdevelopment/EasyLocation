package com.example.locationsample_kotlin_android.location.location_providers

import android.location.Location
import com.example.locationsample_kotlin_android.location.LocationStatus

interface LocationStatusListener {

    fun onLocationRetrieved(location: Location)

    fun onLocationRetrieveError(locationStatus: LocationStatus?)
}
