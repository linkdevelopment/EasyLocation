package com.linkdev.easylocation.location_providers

import android.location.Location

internal interface LocationResultListener {

    fun onLocationRetrieved(location: Location)

    fun onLocationRetrieveError(locationResult: LocationResult?)
}
