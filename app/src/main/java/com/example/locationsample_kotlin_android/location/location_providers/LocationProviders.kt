package com.example.locationsample_kotlin_android.location.location_providers

interface LocationProviders {

    fun requestLocationUpdates(locationStatusListener: LocationStatusListener)

    fun stopLocationUpdates()

    fun fetchLatestKnownLocation()
}
