package com.linkdev.easylocation.location_providers

internal interface LocationProvider {

    fun requestLocationUpdates(locationResultListener: LocationResultListener)

    fun stopLocationUpdates()

    fun fetchLatestKnownLocation()
}
