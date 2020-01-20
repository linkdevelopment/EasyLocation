package com.example.locationsample_kotlin_android.location.location_providers

interface LocationProviders {

    fun requestLocationUpdates()

    fun stopLocationUpdates()

    fun fetchLatestKnownLocation()
}
