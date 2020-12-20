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

import android.Manifest
import android.content.Context
import androidx.annotation.RequiresPermission
import com.linkdev.easylocation.core.location_providers.fused.FusedLocationProvider
import com.linkdev.easylocation.core.location_providers.fused.options.DisplacementLocationOptions
import com.linkdev.easylocation.core.location_providers.fused.options.LocationOptions
import com.linkdev.easylocation.core.location_providers.fused.options.TimeLocationOptions
import com.linkdev.easylocation.core.models.*

/**
 * Use this factory to manage different location providers currently there is only the [FusedLocationProvider]
 * You can [requestLocationUpdates] or [stopLocationUpdates]
 *
 * @param mContext Context
 * @param mMaxLocationRequestTime Optional = [EasyLocationConstants.DEFAULT_LOCATION_REQUEST_TIMEOUT] - The max wait time for the location update after the request is made, If exceeded the request will stop.
 * @param mLocationRequestType Optional = [LocationRequestType.UPDATES] - One of [LocationRequestType.ONE_TIME_REQUEST] or [LocationRequestType.UPDATES]
 */
internal class LocationProvidersFactory(
    private val mContext: Context,
) {

    private lateinit var mILocationProvider: ILocationProvider

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
        locationResultListener: LocationResultListener
    ) {
        when (locationProvider) {
            LocationProvidersTypes.FUSED_LOCATION_PROVIDER -> {
                startFusedLocationUpdates(locationOptions, locationResultListener)
            }
            else -> throw IllegalArgumentException(EasyLocationConstants.ErrorMessages.UNKNOWN_PROVIDER)
        }
    }

    /**
     * fetch the latest known location.
     *
     * @param locationProvider Represents the location provider used to retrieve the location one of [LocationProvidersTypes] enum values.
     */
    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    fun fetchLastKnownLocation(
        locationProvider: LocationProvidersTypes,
        locationResultListener: LocationResultListener
    ) {
        when (locationProvider) {
            LocationProvidersTypes.FUSED_LOCATION_PROVIDER -> {
                fetchFusedLastKnownLocation(locationResultListener)
            }
        }
    }

    /**
     * Cancels the [mILocationProvider] location updates
     */
    fun stopLocationUpdates() {
        if (::mILocationProvider.isInitialized)
            mILocationProvider.stopLocationUpdates()
    }

    /**
     * Initializes the fused location provider and requests the location updates using the provided [locationOptions]
     *
     * @param locationOptions The location options provided to the [FusedLocationProvider]
     * could be one of:
     *  + [DisplacementLocationOptions]
     *  + [TimeLocationOptions]
     *
     *  @param locationResultListener The location callback listener.
     */
    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    private fun startFusedLocationUpdates(
        locationOptions: LocationOptions,
        locationResultListener: LocationResultListener
    ) {
        initializeFusedLocationProvider(locationOptions)
        (mILocationProvider as FusedLocationProvider).requestLocationUpdates(locationResultListener)
    }

    /**
     * Initializes the fused location provider and requests the last known location using the [FusedLocationProvider]
     *
     *  @param locationResultListener The location callback listener.
     */
    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    private fun fetchFusedLastKnownLocation(
        locationResultListener: LocationResultListener
    ) {
        initializeFusedLocationProvider()

        (mILocationProvider as FusedLocationProvider).fetchLatestKnownLocation(
            locationResultListener
        )
    }

    /**
     * Initializes fused location provider.
     */
    private fun initializeFusedLocationProvider(locationOptions: LocationOptions = EasyLocationConstants.DEFAULT_FUSED_OPTIONS) {
        mILocationProvider = FusedLocationProvider(mContext, locationOptions)
    }
}
