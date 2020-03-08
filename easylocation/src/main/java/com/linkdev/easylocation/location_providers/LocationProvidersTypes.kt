package com.linkdev.easylocation.location_providers

/**
 * EasyLocation_Android Created by Mohammed.Fareed on 1/15/2020.
 * * // Copyright (c) 2020 LinkDev. All rights reserved.**/
enum class LocationProvidersTypes {
    /**
     * This Provider uses the FusedLocationProvider and Google Play Services to retrieve location.
     *
     * For more info check [https://developers.google.com/location-context/fused-location-provider]
     */
    FUSED_LOCATION_PROVIDER,
    /**
     * This Provider uses the LocationManager to retrieve locations.
     *
     * For more info check [https://developer.android.com/reference/android/location/LocationManager]
     */
    LOCATION_MANAGER_LOCATION_PROVIDER,
}
