/**
 * Copyright (c) 2020-present Link Development
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.linkdev.easylocation.core

import android.Manifest
import android.content.Context
import android.location.Location
import android.os.Handler
import android.os.Looper
import androidx.annotation.RequiresPermission
import com.linkdev.easylocation.core.location_providers.LocationProvidersFactory
import com.linkdev.easylocation.core.location_providers.LocationResultListener
import com.linkdev.easylocation.core.location_providers.fused.options.LocationOptions
import com.linkdev.easylocation.core.models.*
import com.linkdev.easylocation.core.models.EasyLocationConstants

/**
 * This class Manages the location updates timeout request time, and request type, and
 * manages the [LocationProvidersFactory] to request and stop the location updates.
 *
 * @param mContext Context
 * @param mLocationRequestTimeout Optional = [EasyLocationConstants.DEFAULT_LOCATION_REQUEST_TIMEOUT] - The max wait time for the location update after the request is made, If exceeded the request will stop.
 * @param mLocationRequestType Optional = [LocationRequestType.UPDATES] - One of [LocationRequestType.ONE_TIME_REQUEST] or [LocationRequestType.UPDATES]
 */
internal class EasyLocationManager(
    private val mContext: Context,
    private var mLocationRequestTimeout: Long = EasyLocationConstants.DEFAULT_LOCATION_REQUEST_TIMEOUT,
    private var mLocationRequestType: LocationRequestType = LocationRequestType.UPDATES
) {

    private var mLocationRequestTimeoutHandler: Handler = Handler(Looper.getMainLooper())
    private lateinit var mLocationResultListener: LocationResultListener

    private val mLocationProvidersFactory = LocationProvidersFactory(mContext)

    /**
     * Requests location updates from the [locationProvider] using [locationOptions].
     *
     * @param locationProvider Represents the location provider used to retrieve the location one of [LocationProvidersTypes] enum values.
     *
     * @param locationOptions The specs required for retrieving location info, Depending on [locationProvider]:
     *      + [DisplacementFusedLocationOptions]
     *      + [TimeFusedLocationOptions]
     *
     * @param locationResultListener This listener will be invoked with the updates from the location provider.
     *
     * @throws IllegalArgumentException If the [locationOptions] does not correspond to the selected [LocationProvidersTypes] mentioned above.
     */
    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    fun requestLocationUpdates(
        locationProvider: LocationProvidersTypes,
        locationOptions: LocationOptions,
        locationResultListener: LocationResultListener,
    ) {
        mLocationResultListener = locationResultListener

        mLocationProvidersFactory.requestLocationUpdates(
            locationProvider,
            locationOptions,
            onLocationRetrieved()
        )

        startLocationRequestTimer()
    }

    /**
     * fetch the latest known location using this provider.
     */
    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    fun fetchLatestKnownLocation(
        locationProvider: LocationProvidersTypes,
        locationResultListener: LocationResultListener
    ) {
        mLocationProvidersFactory.fetchLastKnownLocation(locationProvider, locationResultListener)
    }

    /**
     * Cancels the location updates from the factory and stops the timeout timer.
     */
    fun stopLocationUpdates() {
        mLocationProvidersFactory.stopLocationUpdates()

        mLocationRequestTimeoutHandler.removeCallbacks(timeoutRunnable)
    }

    /**
     * Callback for when the location is retrieved to manage the timer and the location updates and update the consumer [mLocationResultListener].
     */
    private fun onLocationRetrieved(): LocationResultListener {
        return object : LocationResultListener {
            override fun onLocationRetrieved(location: Location?) {
                if (mLocationRequestType == LocationRequestType.ONE_TIME_REQUEST)
                    stopLocationUpdates()

                restartTimer()

                if (location != null)
                    mLocationResultListener.onLocationRetrieved(location)
                else
                    mLocationResultListener.onLocationRetrievalError(
                        LocationResult.Error(LocationResultError.UnknownError())
                    )
            }

            override fun onLocationRetrievalError(locationResult: LocationResult?) {
                mLocationResultListener.onLocationRetrievalError(locationResult)

            }
        }
    }

    /**
     * Starts a timer to stop the request of the location updates after [mLocationRequestTimeout] seconds.
     */
    private fun startLocationRequestTimer() {
        mLocationRequestTimeoutHandler.postDelayed(timeoutRunnable, mLocationRequestTimeout)
    }

    /**
     * Restarts the location timer [timeoutRunnable]
     */
    private fun restartTimer() {
        mLocationRequestTimeoutHandler.removeCallbacks(timeoutRunnable)
        mLocationRequestTimeoutHandler.postDelayed(timeoutRunnable, mLocationRequestTimeout)
    }

    private val timeoutRunnable: Runnable = Runnable {
        stopLocationUpdates()
        if (::mLocationResultListener.isInitialized)
            mLocationResultListener.onLocationRetrievalError(
                LocationResult.Error(LocationResultError.TimeoutError())
            )
    }

}
