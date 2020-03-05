package com.linkdev.easylocation.location_providers

internal interface LocationProvider {

    fun requestLocationUpdates(locationStatusListener: LocationStatusListener)

    fun stopLocationUpdates()

    fun fetchLatestKnownLocation()
}
