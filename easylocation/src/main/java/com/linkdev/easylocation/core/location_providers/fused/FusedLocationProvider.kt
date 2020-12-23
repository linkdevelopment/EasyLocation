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
package com.linkdev.easylocation.core.location_providers.fused

import android.Manifest
import android.content.Context
import android.location.Location
import android.os.Looper
import androidx.annotation.RequiresPermission
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.Task
import com.linkdev.easylocation.core.location_providers.ILocationProvider
import com.linkdev.easylocation.core.location_providers.LocationResultListener
import com.linkdev.easylocation.core.location_providers.fused.options.DisplacementLocationOptions
import com.linkdev.easylocation.core.location_providers.fused.options.LocationOptions
import com.linkdev.easylocation.core.location_providers.fused.options.TimeLocationOptions
import com.linkdev.easylocation.core.models.EasyLocationConstants
import com.linkdev.easylocation.core.models.LocationResult
import com.linkdev.easylocation.core.models.LocationResultError

/**
 * This Provider uses the FusedLocationProvider and Google Play Services to retrieve location.
 *
 * For more info check [https://developers.google.com/location-context/fused-location-provider]
 */
internal class FusedLocationProvider(
    private val mContext: Context,
    private val mLocationOptions: LocationOptions = EasyLocationConstants.DEFAULT_FUSED_OPTIONS
) : ILocationProvider {

    private lateinit var mLocationResultListener: LocationResultListener
    private val mFusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(mContext)

    /**
     * Request the location updates for this subscriber [locationResultListener]
     *
     * @param locationResultListener
     */
    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    override fun requestLocationUpdates(locationResultListener: LocationResultListener) {
        mLocationResultListener = locationResultListener

        val locationRequest = createLocationRequest(mLocationOptions)

        mFusedLocationClient.requestLocationUpdates(
            locationRequest,
            mLocationCallback,
            Looper.getMainLooper()
        )
    }

    /**
     * cancel the location updates for the fused provider callback
     */
    override fun stopLocationUpdates() {
        mFusedLocationClient.removeLocationUpdates(mLocationCallback)
    }

    /**
     * fetch the latest known location using this provider.
     */
    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    override fun fetchLatestKnownLocation(locationResultListener: LocationResultListener) {
        mLocationResultListener = locationResultListener

        mFusedLocationClient.lastLocation.addOnCompleteListener { onLastLocationRetrieved(it) }
    }

    /**
     * updates the subscriber with the location on location retrieved.
     */
    private fun onLocationRetrieved(location: Location) {
        mLocationResultListener.onLocationRetrieved(location)
    }

    private val mLocationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: com.google.android.gms.location.LocationResult?) {
            if (locationResult == null) {
                mLocationResultListener.onLocationRetrievalError(
                    LocationResult.Error(LocationResultError.UnknownError())
                )
                return
            }
            locationResult.locations.forEach {
                onLocationRetrieved(it)
            }
        }
    }

    /**
     * Called when the last known location retrieved and notifies the location subscriber [mLocationResultListener]
     */
    private fun onLastLocationRetrieved(locationTask: Task<Location>) {
        if (locationTask.isSuccessful && locationTask.result != null)
            mLocationResultListener.onLocationRetrieved(locationTask.result)
        else
            mLocationResultListener.onLocationRetrievalError(
                LocationResult.Error(
                    LocationResultError.ProviderException(locationTask.exception)
                )
            )
    }

    /**
     * Creates the [FusedLocationProviderClient] [LocationRequest] from the provided [mLocationOptions].
     */
    private fun createLocationRequest(locationOptions: LocationOptions): LocationRequest {
        return when (locationOptions) {
            is DisplacementLocationOptions ->
                LocationRequest.create().apply {
                    this.smallestDisplacement = locationOptions.smallestDisplacement
                    this.interval = locationOptions.fastestInterval
                    this.fastestInterval = locationOptions.fastestInterval
                    this.priority = locationOptions.priority.value
                }
            is TimeLocationOptions ->
                LocationRequest.create().apply {
                    this.interval = locationOptions.interval
                    this.fastestInterval = locationOptions.fastestInterval
                    this.priority = locationOptions.priority.value
                }
            else -> {
                throw Exception(EasyLocationConstants.ErrorMessages.FUSED_OPTIONS_TYPE_ERROR)
            }
        }
    }
}
