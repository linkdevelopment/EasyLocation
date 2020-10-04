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
package com.linkdev.location_android.sample.base

import android.location.Location
import androidx.lifecycle.Observer
import com.linkdev.easylocation.EasyLocation
import com.linkdev.easylocation.location_providers.*
import kotlin.properties.Delegates

/**
 * Created by Mohammed Fareed on 30/5/2019.
 */
abstract class LocationBaseLocationFragment : BaseLocationPermissionsFragment() {

    private lateinit var mEasyLocation: EasyLocation

    private lateinit var mLocationProviderType: LocationProvidersTypes
    private lateinit var mLocationOptions: LocationOptions
    private var mSingleLocationRequest by Delegates.notNull<Boolean>()
    private var mMaxLocationRequestTime by Delegates.notNull<Long>()

    /**
     * Called when both LocationPermission and locationSetting are granted.
     */
    abstract fun onLocationRetrieved(location: Location)

    abstract fun onLocationRetrievalError(locationError: LocationError)

    /**
     * The entry point for [LocationBaseLocationFragment] after calling this method:
     *
     * 1- Location permission will be Checked.
     *
     * 2- Location setting will be Checked.
     *
     * 3- Finally, if previous points are valid will Use [EasyLocationLifeCycleObserver] to retrieve the location
     * and call the [onLocationRetrieved] callback method in case of successful retrieval,
     * Otherwise in case of errors [onLocationRetrievalError] will be called.
     *
     * @param locationProviderType Represents the location provider used to retrieve the location one of [LocationProvidersTypes] enum values.
     * @param locationOptions The specs required for retrieving location info, Depending on [locationProviderType]:
     * - [LocationProvidersTypes.LOCATION_MANAGER_PROVIDER] Should be one of:
     *      + [DisplacementLocationManagerOptions]
     *      + [TimeLocationManagerOptions]
     * - [LocationProvidersTypes.FUSED_LOCATION_PROVIDER] Should be one of:
     *      + [DisplacementFusedLocationOptions]
     *      + [TimeFusedLocationOptions]
     * @param singleLocationRequest true to emit the location only once.
     * @param maxLocationRequestTime Sets the max location updates request time
     *      if exceeded stops the location updates and returns error <P>
     *      @Default{#LocationLifecycleObserver.DEFAULT_MAX_LOCATION_REQUEST_TIME}.
     */
    protected fun getLocation(
        locationProviderType: LocationProvidersTypes,
        locationOptions: LocationOptions,
        singleLocationRequest: Boolean = false,
        maxLocationRequestTime: Long = 50000
    ) {
        mLocationProviderType = locationProviderType
        mLocationOptions = locationOptions
        mSingleLocationRequest = singleLocationRequest
        mMaxLocationRequestTime = maxLocationRequestTime

        checkLocationPermissions(activity)
    }

    override fun onLocationPermissionsReady() {
        getLocation()
    }

    override fun onLocationPermissionError(locationError: LocationError) {
        onLocationRetrievalError(locationError)
    }

    private fun getLocation() {
        mEasyLocation = EasyLocation.Builder(context!!)
            .setMaxLocationRequestTime(mMaxLocationRequestTime)
            .setSingleLocationRequest(mSingleLocationRequest)
            .build()

        mEasyLocation.requestLifecycleLocationUpdates(
            lifecycle,
            mLocationProviderType,
            mLocationOptions
        )
            .observe(this, Observer { locationStatus -> onLocationStatusRetrieved(locationStatus) })
    }

    private fun onLocationStatusRetrieved(locationResult: LocationResult) {
        when (locationResult.status) {
            Status.SUCCESS -> {
                if (locationResult.location == null) {
                    onLocationRetrievalError(LocationError.LOCATION_ERROR)
                    return
                }
                onLocationRetrieved(locationResult.location!!)
            }
            Status.ERROR ->
                onLocationRetrievalError(LocationError.LOCATION_ERROR)
            Status.PERMISSION_NOT_GRANTED -> {
                onLocationRetrievalError(LocationError.LOCATION_PERMISSION_DENIED)
            }
        }
    }

    fun stopLocation() {
        mEasyLocation.stopLocationUpdates()
    }
}
