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
package com.linkdev.easylocation

import android.content.Context
import android.location.Location
import androidx.lifecycle.*
import com.linkdev.easylocation.location_providers.*
import com.linkdev.easylocation.utils.EasyLocationConstants

/**
 * Use this class to listen for location updates .
 *
 * @param lifecycle the lifecycle owner.
 * @param mContext
 */
internal class EasyLocationLifeCycleObserver(
    lifecycle: Lifecycle, private val mContext: Context
) : LifecycleObserver, IEasyLocationObserver {

    /**
     * Sets the max location updates request time
     *      if exceeded stops the location updates and returns error <br/>
     *      @Default [EasyLocationLifeCycleObserver.DEFAULT_MAX_LOCATION_REQUEST_TIME].
     */
    private var mMaxLocationRequestTime: Long =
        EasyLocationConstants.DEFAULT_MAX_LOCATION_REQUEST_TIME

    /**
     * true to emit the location only once.
     */
    private var mSingleLocationRequest: Boolean = false

    private val mLocationResponseLiveData: MutableLiveData<LocationResult> = MutableLiveData()

    private var mLocationProvidersFactory: LocationProvidersFactory =
        LocationProvidersFactory(
            mContext, onLocationResultListener(), mMaxLocationRequestTime, mSingleLocationRequest
        )

    init {
        lifecycle.addObserver(this)
    }

    /**
     * @param locationProviderType Represents the location provider used to retrieve the location one of [LocationProvidersTypes] enum values.
     *
     * @param locationOptions The specs required for retrieving location info, Depending on [locationProviderType]:
     * - [LocationProvidersTypes.LOCATION_MANAGER_PROVIDER] Should be one of:
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
    override fun requestLocationUpdates(
        locationProviderType: LocationProvidersTypes,
        locationOptions: LocationOptions
    ): LiveData<LocationResult> {
        mLocationProvidersFactory.requestLocationUpdates(locationProviderType, locationOptions)

        return mLocationResponseLiveData
    }

    override fun stopLocationUpdates() {
        mLocationProvidersFactory.stopLocationUpdates()
    }

    override fun setMaxLocationRequestTime(maxLocationRequestTime: Long) {
        mMaxLocationRequestTime = maxLocationRequestTime
    }

    override fun setSingleLocationRequest(singleLocationRequest: Boolean) {
        mSingleLocationRequest = singleLocationRequest
    }

    private fun onLocationResultListener(): LocationResultListener {
        return object : LocationResultListener {
            override fun onLocationRetrieved(location: Location) {
                if (mSingleLocationRequest)
                    stopLocationUpdates()
                mLocationResponseLiveData.value = LocationResult.Success(location)
            }

            override fun onLocationRetrievalError(locationResult: LocationResult?) {
                if (mSingleLocationRequest)
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
