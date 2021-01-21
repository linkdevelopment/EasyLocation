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

import android.Manifest
import android.app.Notification
import android.app.Service
import android.content.Intent
import android.content.res.Configuration
import android.os.Binder
import android.os.IBinder
import androidx.annotation.RequiresPermission
import com.linkdev.easylocation.core.EasyLocationManager
import com.linkdev.easylocation.core.location_providers.LocationResultListener
import com.linkdev.easylocation.core.location_providers.fused.options.LocationOptions
import com.linkdev.easylocation.core.models.EasyLocationConstants
import com.linkdev.easylocation.core.models.EasyLocationRequest
import com.linkdev.easylocation.core.utils.EasyLocationNotification

/**
 * A started and bound service that self promotes to a foreground service
 * when all the clients unbound and there is a location request running.
 */
internal class EasyLocationForegroundService : Service() {

    /**
     * The notification used when the service is being promoted to a foreground notification.
     */
    private lateinit var mNotification: Notification

    /**
     * The binder used for binding to the service.
     *
     * @see LocalBinder
     */
    private val mBinder: IBinder = LocalBinder()

    private lateinit var mEasyLocationManager: EasyLocationManager

    /**
     * Used to check whether the bound activity has really gone away and not unbound as part of an
     * orientation change.
     * We create a foreground service notification only if the former takes place.
     */
    private var mChangingConfiguration = false
    private var requestingLocation: Boolean = false
    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        mNotification =
            intent.getParcelableExtra(EasyLocationConstants.EXTRA_NOTIFICATION)
                ?: EasyLocationNotification().notification(applicationContext,)

        val easyLocationRequest =
            intent.getParcelableExtra<EasyLocationRequest>(EasyLocationConstants.EXTRA_EASY_LOCATION_REQUEST)

        if (easyLocationRequest != null)
            mEasyLocationManager = EasyLocationManager(
                applicationContext,
                easyLocationRequest.locationRequestTimeout,
                easyLocationRequest.locationRequestType
            )

        // Tells the system to not try to recreate the service after it has been killed.
        return START_NOT_STICKY
    }

    /**
     * get to know when there is a orientation change to not promote the service is this case.
     */
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        mChangingConfiguration = true
    }

    /**
     * When the client binds to this service it should cease to be a foreground service.
     */
    override fun onBind(intent: Intent): IBinder {
        stopForeground(true)
        mChangingConfiguration = false
        return mBinder
    }

    /**
     * When a client rebinds to this service it should cease to be a foreground service.
     */
    override fun onRebind(intent: Intent) {
        stopForeground(true)
        mChangingConfiguration = false
        super.onRebind(intent)
    }

    /**
     * when the last client unbinds from this service if it's not an orientation change and if there is a location request running,
     * The service self promotes to a foreground service.
     */
    override fun onUnbind(intent: Intent): Boolean {
        if (!mChangingConfiguration && requestingLocation) {
            startForeground(EasyLocationNotification.NOTIFICATION_ID, mNotification)
        }
        return true // Ensures onRebind() is called when a client re-binds.
    }

    /**
     * Makes a request for location updates.
     */
    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    fun requestLocationUpdates(
        locationOptions: LocationOptions,
        onLocationResultListener: LocationResultListener
    ) {
        requestingLocation = true

        mEasyLocationManager.requestLocationUpdates(locationOptions, onLocationResultListener)
    }

    /**
     * Removes location updates.
     */
    fun removeLocationUpdates() {
        requestingLocation = false
        mEasyLocationManager.stopLocationUpdates()
        stopSelf()
    }

    /**
     * Class used for the client Binder.
     * Since this service runs in the same process as its clients, we don't need to deal with IPC.
     */
    inner class LocalBinder : Binder() {
        fun getService(): EasyLocationForegroundService = this@EasyLocationForegroundService
    }
}