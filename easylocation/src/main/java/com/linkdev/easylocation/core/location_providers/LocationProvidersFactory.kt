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
package com.linkdev.easylocation.core.location_providers

import android.content.Context
import android.location.Location
import android.os.Handler
import android.os.Looper
import com.linkdev.easylocation.core.location_providers.fused.FusedLocationProvider
import com.linkdev.easylocation.core.location_providers.fused.options.DisplacementLocationOptions
import com.linkdev.easylocation.core.location_providers.fused.options.LocationOptions
import com.linkdev.easylocation.core.location_providers.fused.options.TimeLocationOptions
import com.linkdev.easylocation.core.models.*
import com.linkdev.easylocation.core.models.EasyLocationConstants

/**
 * Use this factory to manage different location providers currently there is only the [FusedLocationProvider]
 * You can [requestLocationUpdates] or [stopLocationUpdates]
 *
 * @param mContext Context
 * @param mLocationResultListener This listener will be invoked with the updates from the location provider.
 * @param mMaxLocationRequestTime Optional = [EasyLocationConstants.DEFAULT_MAX_LOCATION_REQUEST_TIME] - The max wait time for the location update after the request is made, If exceeded the request will stop.
 * @param mLocationRequestType Optional = [LocationRequestType.UPDATES] - One of [LocationRequestType.ONE_TIME_REQUEST] or [LocationRequestType.UPDATES]
 */
internal class LocationProvidersFactory(
    private val mContext: Context,
    private val mLocationResultListener: LocationResultListener,
    private var mMaxLocationRequestTime: Long = EasyLocationConstants.DEFAULT_MAX_LOCATION_REQUEST_TIME,
    private var mLocationRequestType: LocationRequestType = LocationRequestType.UPDATES
) {

    private lateinit var mILocationProvider: ILocationProvider
    private var mLocationRequestTimeoutHandler: Handler = Handler(Looper.getMainLooper())

    /**
     * Requests location updates from the [locationProvider] using [locationOptions].
     *
     * @param locationProvider Represents the location provider used to retrieve the location one of [LocationProvidersTypes] enum values.
     *
     * @param locationOptions The specs required for retrieving location info, Depending on [locationProvider]:
     *      + [DisplacementFusedLocationOptions]
     *      + [TimeFusedLocationOptions]
     *
     * @throws IllegalArgumentException If the [locationOptions] does not correspond to the selected [LocationProvidersTypes] mentioned above.
     */
    fun requestLocationUpdates(
        locationProvider: LocationProvidersTypes,
        locationOptions: LocationOptions
    ) {
        when (locationProvider) {
            LocationProvidersTypes.FUSED_LOCATION_PROVIDER -> {
                startFusedLocationUpdates(locationOptions)
            }
        }
    }

    /**
     * Creates location updates request with the fused location provider [LocationProvidersTypes.FUSED_LOCATION_PROVIDER].
     *
     * @param LocationOptions could be one of:
     *  + [DisplacementLocationOptions]
     *  + [TimeLocationOptions]
     *
     * @return LiveData object to listen for location updates with [LocationResult].
     */
    private fun startFusedLocationUpdates(LocationOptions: LocationOptions) {
        startLocationRequestTimer()
        requestFusedLocationUpdates(LocationOptions)
    }

    private fun requestFusedLocationUpdates(LocationOptions: LocationOptions) {
        mILocationProvider = FusedLocationProvider(mContext, LocationOptions)
        (mILocationProvider as FusedLocationProvider).requestLocationUpdates(onLocationRetrieved())
    }

    private fun onLocationRetrieved(): LocationResultListener {
        return object : LocationResultListener {
            override fun onLocationRetrieved(location: Location) {
                if (mLocationRequestType == LocationRequestType.ONE_TIME_REQUEST)
                    stopLocationUpdates()

                mLocationRequestTimeoutHandler.removeCallbacks(runnable)
                mLocationRequestTimeoutHandler.postDelayed(runnable, mMaxLocationRequestTime)

                mLocationResultListener.onLocationRetrieved(location)
            }

            override fun onLocationRetrievalError(locationResult: LocationResult?) {
                mLocationResultListener.onLocationRetrievalError(locationResult)
            }
        }
    }

    fun stopLocationUpdates() {
        if (::mILocationProvider.isInitialized)
            mILocationProvider.stopLocationUpdates()
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
        mLocationResultListener.onLocationRetrievalError(LocationResult.Error(LocationResultError.TimeoutError()))
    }
}
