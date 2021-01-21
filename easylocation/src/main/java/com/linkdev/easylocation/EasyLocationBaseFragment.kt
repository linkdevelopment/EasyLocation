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

import android.Manifest
import android.annotation.SuppressLint
import android.app.Notification
import android.location.Location
import androidx.annotation.RequiresPermission
import com.linkdev.easylocation.core.bases.BaseLocationPermissionsFragment
import com.linkdev.easylocation.core.location_providers.fused.options.LocationOptions
import com.linkdev.easylocation.core.models.LocationRequestType
import com.linkdev.easylocation.core.models.LocationResult
import com.linkdev.easylocation.core.models.LocationResultError
import com.linkdev.easylocation.core.models.Status
import kotlin.properties.Delegates

/**
 * The easiest way to implement Easy Location is by extending this fragment, This will handle the location settings and permissions.
 *
 * Uses [EasyLocation] under the hood, If you want to handle the location permission and settings yourself use [EasyLocation] directly.
 *
 * You will call [getLocation]
 *
 * You will implement abstract [onLocationRetrieved] and [onLocationRetrievalError] as callbacks for when the location is updated or errors.
 *
 * That's it.
 */
abstract class EasyLocationBaseFragment : BaseLocationPermissionsFragment() {

    private lateinit var mEasyLocation: EasyLocation
    private lateinit var mLocationOptions: LocationOptions

    private var mLocationRequestType by Delegates.notNull<LocationRequestType>()
    private var mLocationRequestTimeout by Delegates.notNull<Long>()

    private var mNotification: Notification? = null
    private var mNotificationID: Int = 123456

    /**
     * Called when both LocationPermission and locationSetting are granted.
     */
    abstract fun onLocationRetrieved(location: Location)

    /**
     * Called when the location retrieved with an error.
     */
    abstract fun onLocationRetrievalError(locationResultError: LocationResultError)

    /**
     * The entry point for [EasyLocationBaseFragment] after calling this method:
     *
     * 1- Location permission will be Checked.
     *
     * 2- Location setting will be Checked.
     *
     * 3- Finally, if previous points are valid will Use [EasyLocation] to retrieve the location
     * and call the [onLocationRetrieved] callback method in case of successful retrieval,
     * Otherwise in case of errors [onLocationRetrievalError] will be called.
     *
     * @param locationOptions The specs required for retrieving location info:
     *      + [DisplacementFusedLocationOptions]
     *      + [TimeFusedLocationOptions]
     *
     * @param locationRequestType true to emit the location only once.
     *
     * @param locationRequestTimeout Sets the max location updates request time
     *      if exceeded stops the location updates and returns error <P>
     *      @Default{#LocationLifecycleObserver.DEFAULT_MAX_LOCATION_REQUEST_TIME}.
     */
    protected fun getLocation(
        locationOptions: LocationOptions,
        locationRequestType: LocationRequestType = LocationRequestType.UPDATES,
        locationRequestTimeout: Long = 50000,
        notification: Notification? = null,
        notificationID: Int = 123456,
        rationaleDialogMessage: String = getString(R.string.easy_location_rationale_message)
    ) {
        mLocationOptions = locationOptions
        mLocationRequestType = locationRequestType
        mLocationRequestTimeout = locationRequestTimeout
        mNotification = notification
        mNotificationID = notificationID

        checkLocationPermissions(requireActivity(), rationaleDialogMessage)
    }

    @SuppressLint("MissingPermission")
    override fun onLocationPermissionsReady() {
        getLocation()
    }

    override fun onLocationPermissionError(locationResultError: LocationResultError) {
        onLocationRetrievalError(locationResultError)
    }

    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    private fun getLocation() {
        val builder = EasyLocation.Builder(context!!, mLocationOptions)
            .setLocationRequestTimeout(mLocationRequestTimeout)
            .setLocationRequestType(mLocationRequestType)

        if (mNotification != null)
            builder.setCustomNotification(mNotificationID, mNotification!!)

        mEasyLocation = builder.build()
        mEasyLocation.requestLocationUpdates(lifecycle)
            .observe(this, this::onLocationStatusRetrieved)
    }

    private fun onLocationStatusRetrieved(locationResult: LocationResult) {
        when (locationResult.status) {
            Status.SUCCESS -> {
                onLocationRetrieved(locationResult.location!!)
            }
            Status.ERROR ->
                onLocationRetrievalError(
                    locationResult.locationResultError ?: LocationResultError.UnknownError()
                )
        }
    }

    fun stopLocation() {
        if (::mEasyLocation.isInitialized)
            mEasyLocation.stopLocationUpdates()
    }

    override fun onDestroy() {
        stopLocation()
        super.onDestroy()
    }
}
