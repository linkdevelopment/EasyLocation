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
import android.app.Notification
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.location.Location
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import androidx.lifecycle.*
import com.linkdev.easylocation.IEasyLocationObserver
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
 * @param mLocationRequestTimeout Sets the max location updates request time after calling [requestLocationUpdates], if exceeded without any location updates un-subscribes and returns error.
 * set to [EasyLocationConstants.INFINITE_REQUEST_TIME] to never stop listening for updates.
 * @param mLocationRequestType sets the request type [LocationRequestType.ONE_TIME_REQUEST] or [LocationRequestType.UPDATES]
 */
internal class EasyLocationLifeCycleObserver(
    private val mContext: Context,
    private val mLocationRequestTimeout: Long = EasyLocationConstants.DEFAULT_LOCATION_REQUEST_TIMEOUT,
    private val mLocationRequestType: LocationRequestType = LocationRequestType.UPDATES,
    private val mNotification: Notification,
) : LifecycleObserver, IEasyLocationObserver {

    // A reference to the service used to get location updates.
    private var mEasyLocationForegroundService: EasyLocationForegroundService? = null

    // Tracks the bound state of the service.
    private var mBound = false

    // The intent used to start the Foreground service
    private val mIntent = Intent(mContext, EasyLocationForegroundService::class.java).apply {
        putExtra(
            EasyLocationConstants.EXTRA_EASY_LOCATION_REQUEST,
            EasyLocationRequest(mLocationRequestTimeout, mLocationRequestType)
        )
        putExtra(
            EasyLocationConstants.EXTRA_NOTIFICATION,
            mNotification
        )
    }

    // The location request received from the client
    private lateinit var mLocationOptions: LocationOptions

    // Monitors the state of the connection to the service.
    private val mOnServiceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            val binder: EasyLocationForegroundService.LocalBinder =
                service as EasyLocationForegroundService.LocalBinder

            mEasyLocationForegroundService = binder.getService()
            mBound = true
        }

        override fun onServiceDisconnected(name: ComponentName) {
            mEasyLocationForegroundService = null
            mBound = false
        }
    }

    /**
     * Bind to the service.
     * If the service is in foreground mode, this signals to the service
     * that since this lifecycle component is in the foreground, the service can promote to foreground mode.
     */
    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onStart() {
        mContext.bindService(mIntent, mOnServiceConnection, Context.BIND_AUTO_CREATE)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onStop() {
        if (mBound) {
            // Unbind from the service. This signals to the service that this activity is no longer
            // in the foreground, and the service can respond by promoting itself to a foreground
            // service.
            mContext.unbindService(mOnServiceConnection)
            mBound = false
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy() {
        stopLocationUpdates()
    }

    /**
     * The liveData used to emit the location updates
     */
    private val mLocationResponseLiveData: MutableLiveData<LocationResult> = MutableLiveData()

    /**
     * Requests location updates using [locationOptions] and returns [LocationResult].
     *
     * This method starts the service and after a delayed 200 millis it starts the location updates,
     * The delay is to give the service a chance to start.
     *
     * @param locationOptions The specs required for retrieving location info should be one of
     *      + [DisplacementFusedLocationOptions]
     *      + [TimeFusedLocationOptions]
     *
     * @return LiveData object to listen for location updates with [LocationResult].
     */
    override fun requestLocationUpdates(locationOptions: LocationOptions): LiveData<LocationResult> {
        mLocationOptions = locationOptions

        mContext.startService(mIntent)
        Handler(Looper.getMainLooper()).postDelayed(
            { startLocationUpdates(mLocationOptions) },
            EasyLocationConstants.FOREGROUND_SERVICE_LOCATION_REQUEST_DELAY
        )

        return mLocationResponseLiveData
    }

    /**
     * Stops and cancels the location updates.
     */
    override fun stopLocationUpdates() {
        mEasyLocationForegroundService?.removeLocationUpdates()
    }

    /**
     * Requests the location updates
     *
     * @param locationOptions The specs required for retrieving location info should be one of
     *      + [DisplacementFusedLocationOptions]
     *      + [TimeFusedLocationOptions]
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

        mEasyLocationForegroundService?.requestLocationUpdates(
            locationOptions,
            onLocationResultListener()
        )
    }

    /**
     * The subscriber for the location used to emit the location result
     */
    private fun onLocationResultListener(): LocationResultListener {
        return object : LocationResultListener {
            override fun onLocationRetrieved(location: Location) {
                emitLocationResponse(LocationResult.Success(location))
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
}
