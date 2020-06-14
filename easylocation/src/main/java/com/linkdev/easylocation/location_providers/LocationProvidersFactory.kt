package com.linkdev.easylocation.location_providers

import android.content.Context
import android.os.Handler
import com.linkdev.easylocation.EasyLocationConstants
import com.linkdev.easylocation.location_providers.fused.DisplacementFusedLocationOptions
import com.linkdev.easylocation.location_providers.fused.FusedLocationProvider
import com.linkdev.easylocation.location_providers.fused.TimeFusedLocationOptions
import com.linkdev.easylocation.location_providers.location_manager.DisplacementLocationManagerOptions
import com.linkdev.easylocation.location_providers.location_manager.LocationManagerProvider
import com.linkdev.easylocation.location_providers.location_manager.LocationManagerOptions

/**
 * LocationSampleKotlin_android Created by Mohammed.Fareed on 3/8/2020.
 * * // Copyright (c) 2020 LinkDev. All rights reserved.**/
internal class LocationProvidersFactory(private val mContext: Context,
                                        private val mLocationResultListener: LocationResultListener,
                                        private var mMaxLocationRequestTime: Long = EasyLocationConstants.DEFAULT_MAX_LOCATION_REQUEST_TIME,
                                        private var mSingleLocationRequest: Boolean = false) {

    private lateinit var mLocationProvider: LocationProvider
    private var mLocationRequestTimeoutHandler: Handler = Handler()

    /**
     * @param locationProviderType Represents the location provider used to retrieve the location one of [LocationProvidersTypes] enum values.
     *
     * @param locationOptions The specs required for retrieving location info, Depending on [locationProviderType]:
     * - [LocationProvidersTypes.LOCATION_MANAGER_LOCATION_PROVIDER] Should be one of:
     *      + [DisplacementLocationManagerOptions]
     *      + [TimeLocationManagerOptions]
     * - [LocationProvidersTypes.FUSED_LOCATION_PROVIDER] Should be one of:
     *      + [DisplacementFusedLocationOptions]
     *      + [TimeFusedLocationOptions]
     *
     * @return LiveData object to listen for location updates with [LocationResult].
     *
     * @throws IllegalArgumentException If the [locationOptions] does not correspond to the selected [LocationProvidersTypes] mentioned above.
     */
    fun requestLocationUpdates(locationProviderType: LocationProvidersTypes, locationOptions: LocationOptions) {
        when (locationProviderType) {
            LocationProvidersTypes.FUSED_LOCATION_PROVIDER -> {
                if (locationOptions !is DisplacementFusedLocationOptions) {
                    throw IllegalArgumentException("Fused location provider options not found should be [FusedLocationOptions]")
                }
                startFusedLocationUpdates(locationOptions)
            }
            LocationProvidersTypes.LOCATION_MANAGER_LOCATION_PROVIDER -> {
                if (locationOptions !is DisplacementLocationManagerOptions) {
                    throw IllegalArgumentException("LocationManager location provider options not found should be one of [LocationManagerOptions]")
                }
                startLocationManagerUpdates(locationOptions)
            }
        }
    }

    /**
     * Creates location updates request with the fused location provider [LocationProvidersTypes.FUSED_LOCATION_PROVIDER].
     *
     * @param fusedLocationOptions could be one of:
     *  + [DisplacementFusedLocationOptions]
     *  + [TimeFusedLocationOptions]
     *
     * @return LiveData object to listen for location updates with [LocationResult].
     */
    private fun startFusedLocationUpdates(fusedLocationOptions: LocationOptions) {
        if (mSingleLocationRequest)
            startLocationRequestTimer()
        requestFusedLocationUpdates(fusedLocationOptions)
    }

    /**
     * Creates location updates request with the LocationManager location provider [LocationProvidersTypes.LOCATION_MANAGER_LOCATION_PROVIDER].
     *
     * @return LiveData object to listen for location updates with [LocationResult].
     */
    private fun startLocationManagerUpdates(locationManagerOptions: LocationManagerOptions) {
        if (mSingleLocationRequest)
            startLocationRequestTimer()
        requestLocationManagerUpdates(locationManagerOptions)
    }

    private fun requestFusedLocationUpdates(fusedLocationOptions: LocationOptions) {
        mLocationProvider = FusedLocationProvider(mContext, fusedLocationOptions)
        mLocationProvider.requestLocationUpdates(mLocationResultListener)
    }

    private fun requestLocationManagerUpdates(locationManagerOptions: LocationManagerOptions) {
        mLocationProvider = LocationManagerProvider(mContext, locationManagerOptions)
        mLocationProvider.requestLocationUpdates(mLocationResultListener)
    }

    fun stopLocationUpdates() {
        if (::mLocationProvider.isInitialized)
            mLocationProvider.stopLocationUpdates()
        mLocationRequestTimeoutHandler.removeCallbacks(runnable)
    }

    /**
     * Starts a timer to stop the request of the location updates after [mMaxLocationRequestTime] seconds.
     */
    private fun startLocationRequestTimer() {
        mLocationRequestTimeoutHandler.postDelayed(runnable, mMaxLocationRequestTime)
    }

    private val runnable: Runnable = Runnable {
        stopLocationUpdates()
        mLocationProvider.fetchLatestKnownLocation()
    }
}