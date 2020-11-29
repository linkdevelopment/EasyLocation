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

import android.content.Context
import android.location.Location
import androidx.lifecycle.*
import com.linkdev.easylocation.IEasyLocationObserver
import com.linkdev.easylocation.core.location_providers.LocationProvidersFactory
import com.linkdev.easylocation.core.location_providers.LocationResultListener
import com.linkdev.easylocation.core.location_providers.fused.options.LocationOptions
import com.linkdev.easylocation.core.models.EasyLocationConstants
import com.linkdev.easylocation.core.models.LocationProvidersTypes
import com.linkdev.easylocation.core.models.LocationRequestType
import com.linkdev.easylocation.core.models.LocationResult

/**
 * Use this class to listen for location updates .
 *
 * @param mContext
 * @param mMaxLocationRequestTime Sets the max location updates request time after calling [requestLocationUpdates], if exceeded without any location updates un-subscribes and returns error.
 * set to [EasyLocationConstants.INFINITE_REQUEST_TIME] to never stop listening for updates.
 * @param mLocationRequestType sets the request type [LocationRequestType.ONE_TIME_REQUEST] or [LocationRequestType.UPDATES]
 */
internal class EasyLocationLifeCycleObserver(
    private val mContext: Context,
    private val mMaxLocationRequestTime: Long = EasyLocationConstants.DEFAULT_MAX_LOCATION_REQUEST_TIME,
    private val mLocationRequestType: LocationRequestType = LocationRequestType.UPDATES
) : LifecycleObserver, IEasyLocationObserver {

    private val mLocationResponseLiveData: MutableLiveData<LocationResult> = MutableLiveData()

    private var mLocationProvidersFactory: LocationProvidersFactory =
        LocationProvidersFactory(
            mContext, onLocationResultListener(), mMaxLocationRequestTime, mLocationRequestType
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
    override fun requestLocationUpdates(
        locationOptions: LocationOptions
    ): LiveData<LocationResult> {
        startLocationUpdates(locationOptions)

        return mLocationResponseLiveData
    }

    private fun startLocationUpdates(
        locationOptions: LocationOptions
    ) {
        mLocationProvidersFactory.requestLocationUpdates(
            LocationProvidersTypes.FUSED_LOCATION_PROVIDER,
            locationOptions
        )
    }

    override fun stopLocationUpdates() {
        mLocationProvidersFactory.stopLocationUpdates()
    }

    private fun onLocationResultListener(): LocationResultListener {
        return object : LocationResultListener {
            override fun onLocationRetrieved(location: Location) {
                if (mLocationRequestType == LocationRequestType.ONE_TIME_REQUEST)
                    stopLocationUpdates()
                mLocationResponseLiveData.value = LocationResult.Success(location)
            }

            override fun onLocationRetrievalError(locationResult: LocationResult?) {
                if (mLocationRequestType == LocationRequestType.ONE_TIME_REQUEST)
                    stopLocationUpdates()
                mLocationResponseLiveData.value = locationResult
            }
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy() {
        stopLocationUpdates()
    }
}
