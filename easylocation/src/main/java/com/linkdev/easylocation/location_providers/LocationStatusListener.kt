package com.linkdev.easylocation.location_providers

import android.location.Location
import com.linkdev.easylocation.LocationStatus

internal interface LocationStatusListener {

    fun onLocationRetrieved(location: Location)

    fun onLocationRetrieveError(locationStatus: LocationStatus?)
}
