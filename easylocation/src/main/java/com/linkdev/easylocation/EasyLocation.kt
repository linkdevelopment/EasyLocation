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

import android.app.Notification
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LiveData
import com.linkdev.easylocation.core.location_providers.fused.options.LocationOptions
import com.linkdev.easylocation.core.models.EasyLocationConstants
import com.linkdev.easylocation.core.models.LocationRequestType
import com.linkdev.easylocation.core.models.LocationResult
import com.linkdev.easylocation.core.utils.EasyLocationNotification
import com.linkdev.easylocation.lifecycle.EasyLocationLifeCycleObserver

/**
 * EasyLocation is built to ease the frequent task of getting the user's location by using just a few lines of code, But providing a powerful, wide and compact features too.
 *
 * Has a private constructor to initialize [EasyLocation] you should use It's [EasyLocation.Builder]
 */
class EasyLocation private constructor(
    private val mContext: Context,
    private val mLocationOptions: LocationOptions,
    private val mLocationRequestTimeout: Long,
    private val mLocationRequestType: LocationRequestType,
    private val mNotification: Notification
) {

    /**
     * The contract for the location observer {[EasyLocationLifeCycleObserver]}.
     */
    private var mLocationObserver: IEasyLocationObserver? = null

    /**
     * Requests the location using a [lifecycle] registry to handle the location stop on it's own.
     *
     * @param lifecycle The lifecycle that we should attach the location request to,
     *                  So that we stop the location request if The lifecycle
     *                  component is destroyed or dismissed.
     */
    fun requestLocationUpdates(lifecycle: Lifecycle): LiveData<LocationResult> {
        mLocationObserver = EasyLocationLifeCycleObserver(
            mContext, mLocationRequestTimeout, mLocationRequestType, mNotification
        )

        lifecycle.addObserver(mLocationObserver as EasyLocationLifeCycleObserver)

        return mLocationObserver?.requestLocationUpdates(mLocationOptions)!!
    }

    /**
     * If invoked stops the location updates.
     */
    fun stopLocationUpdates() {
        mLocationObserver?.stopLocationUpdates()
    }

    /**
     * This Builder is used to initialize the [EasyLocation] to register for location updates.
     */
    class Builder(private val mContext: Context, private val mLocationOptions: LocationOptions) {

        /**
         * The notification used in the notification helper
         */
        private var mNotification: Notification = EasyLocationNotification().notification(mContext,)

        /**
         * Max location request time before timeout and sending location update failed if the location was not retrieved.
         * use [EasyLocationConstants.INFINITE_REQUEST_TIME] to never revoke the location updates listener.
         */
        private var mLocationRequestTimeout: Long =
            EasyLocationConstants.DEFAULT_LOCATION_REQUEST_TIMEOUT

        /**
         * The location request type [LocationRequestType]
         */
        private var mLocationRequestType: LocationRequestType =
            EasyLocationConstants.DEFAULT_LOCATION_REQUEST_TYPE

        /**
         * Max location request time before timeout and sending location update failed if the location was not retrieved.
         * use [EasyLocationConstants.INFINITE_REQUEST_TIME] to never revoke the location updates listener.
         */
        fun setLocationRequestTimeout(locationRequestTimeout: Long): Builder {
            mLocationRequestTimeout = locationRequestTimeout
            return this
        }

        /**
         * The location request type [LocationRequestType]
         */
        fun setLocationRequestType(locationRequestType: LocationRequestType): Builder {
            mLocationRequestType = locationRequestType
            return this
        }

        /**
         * Set the notification for the foreground service with your own custom notification as opposed to [setNotification].
         *
         * @param notificationID The notificationID used to show the location foreground service.
         * @param notification Your custom notification.
         *
         * @see [setNotification]
         */
        fun setCustomNotification(notificationID: Int, notification: Notification): Builder {
            mNotification = notification
            EasyLocationNotification.NOTIFICATION_ID = notificationID
            return this
        }

        /**
         * Set the notification for the foreground service params directly.
         *
         * @param notificationID the notificationID used to show the location foreground service.
         * @param notificationTitle The notification title.
         * @param notificationMessage The notification message.
         * @param channelID The channel created for the notification.
         *
         * @see setCustomNotification
         */
        fun setNotification(
            notificationID: Int,
            notificationTitle: String,
            notificationMessage: String,
            icon: Int,
            channelID: String,
            action1: NotificationCompat.Action? = null,
            action2: NotificationCompat.Action? = null,
            action3: NotificationCompat.Action? = null,
        ): Builder {
            mNotification = EasyLocationNotification()
                .notification(
                    mContext,
                    notificationTitle,
                    notificationMessage,
                    icon,
                    channelID,
                    notificationID,
                    action1,
                    action2,
                    action3
                )
            return this
        }

        /**
         * Executes the builder and initializes the [EasyLocation] object using the provided parameters.
         */
        fun build(): EasyLocation {
            return EasyLocation(
                mContext,
                mLocationOptions,
                mLocationRequestTimeout,
                mLocationRequestType,
                mNotification
            )
        }
    }
}
