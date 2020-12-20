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
package com.linkdev.easylocation.lifecycle

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import androidx.lifecycle.*
import com.linkdev.easylocation.IEasyLocationObserver
import com.linkdev.easylocation.core.EasyLocationManager
import com.linkdev.easylocation.core.location_providers.LocationResultListener
import com.linkdev.easylocation.core.location_providers.fused.options.LocationOptions
import com.linkdev.easylocation.core.models.*
import com.linkdev.easylocation.core.utils.EasyLocationUtils

/**
 * This class is used to manage the location request lifecycle by
 * Calling [stopLocationUpdates] onDestroy.
 * Exposing the [requestLocationUpdates] to listen for the device location updates.
 *
 * @param mContext
 * @param mMaxLocationRequestTime Sets the max location updates request time after calling [requestLocationUpdates], if exceeded without any location updates un-subscribes and returns error.
 * set to [EasyLocationConstants.INFINITE_REQUEST_TIME] to never stop listening for updates.
 * @param mLocationRequestType sets the request type [LocationRequestType.ONE_TIME_REQUEST] or [LocationRequestType.UPDATES]
 */
internal class EasyLocationLifeCycleObserver(
    private val mContext: Context,
    private val mMaxLocationRequestTime: Long = EasyLocationConstants.DEFAULT_LOCATION_REQUEST_TIMEOUT,
    private val mLocationRequestType: LocationRequestType = LocationRequestType.UPDATES
) : LifecycleObserver, IEasyLocationObserver {

    /**
     * The liveData used to subscribe to emit the location updates
     */
    private val mLocationResponseLiveData: MutableLiveData<LocationResult> = MutableLiveData()

    /**
     * The locationProvidersFactory used to request/stop the location updates using a certain provider.
     */
    private var mEasyLocationManager: EasyLocationManager =
        EasyLocationManager(
            mContext, mMaxLocationRequestTime, mLocationRequestType
        )

    /**
     * Requests location updates from the [locationProvider] using [locationOptions] and returns [LocationResult].
     *
     * @param locationProvider Represents the location provider used to retrieve the location one of [LocationProvidersTypes] enum values.
     *
     * @param locationOptions The specs required for retrieving location info, Depending on [locationProvider]:
     *      + [DisplacementFusedLocationOptions]
     *      + [TimeFusedLocationOptions]
     *
     * @return LiveData object to listen for location updates with [LocationResult].
     *
     * @throws IllegalArgumentException If the [locationOptions] does not correspond to the selected [LocationProvidersTypes] mentioned above.
     */
    override fun requestLocationUpdates(locationOptions: LocationOptions): LiveData<LocationResult> {
        startLocationUpdates(locationOptions)

        return mLocationResponseLiveData
    }

    /**
     * Stops and cancels the location updates.
     */
    override fun stopLocationUpdates() {
        mEasyLocationManager.stopLocationUpdates()
    }

    /**
     * Fetch the latest known location and returns the liveData for locationUpdates.
     */
    override fun fetchLatestKnownLocation(): LiveData<LocationResult> {
        fetchLastKnownLocation()

        return mLocationResponseLiveData
    }

    @SuppressLint("MissingPermission")
    private fun fetchLastKnownLocation() {
        if (!EasyLocationUtils.isLocationPermissionGranted(mContext)) {
            emitLocationResponse(LocationResult.Error(LocationResultError.PermissionDenied()))
            return
        }

        if (!EasyLocationUtils.checkLocationSettings(mContext)) {
            emitLocationResponse(LocationResult.Error(LocationResultError.SettingDisabled()))
            return
        }

        mEasyLocationManager.fetchLatestKnownLocation(
            LocationProvidersTypes.FUSED_LOCATION_PROVIDER,
            onLocationResultListener()
        )
    }

    /**
     * Requests the location updates
     */
    @SuppressLint("MissingPermission")
    private fun startLocationUpdates(locationOptions: LocationOptions) {
        if (!EasyLocationUtils.isLocationPermissionGranted(mContext)) {
            emitLocationResponse(LocationResult.Error(LocationResultError.PermissionDenied()))
            return
        }

        if (!EasyLocationUtils.checkLocationSettings(mContext)) {
            emitLocationResponse(LocationResult.Error(LocationResultError.SettingDisabled()))
            return
        }

        mEasyLocationManager.requestLocationUpdates(
            LocationProvidersTypes.FUSED_LOCATION_PROVIDER,
            locationOptions,
            onLocationResultListener()
        )
    }

    /**
     * The subscriber for the location used to emit the location result
     */
    private fun onLocationResultListener(): LocationResultListener {
        return object : LocationResultListener {
            override fun onLocationRetrieved(location: Location?) {
                emitLocationResponse(LocationResult.Success(location!!))
            }

            override fun onLocationRetrievalError(locationResult: LocationResult?) {
                emitLocationResponse(locationResult)
            }
        }
    }

    /**
     * Emit this [locationResult] to [mLocationResponseLiveData]
     */
    private fun emitLocationResponse(locationResult: LocationResult?) {
        mLocationResponseLiveData.value = locationResult
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy() {
        stopLocationUpdates()
    }
}
