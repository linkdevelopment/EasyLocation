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
package com.linkdev.easylocation.core.utils

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import com.linkdev.easylocation.R

/**
 * Used to construct the notifications for the foreground service
 */
internal class EasyLocationNotification {

    companion object {
        /**
         * The notificationID used for the foregroundService
         */
        var NOTIFICATION_ID = 147852369
    }

    /**
     * Channel id for O+ versions.
     */
    private lateinit var mChannelID: String

    /**
     * Returns default notification
     */
    fun notification(
        context: Context,
        title: String = context.getString(R.string.notificationTitle),
        message: String = context.getString(R.string.notificationMessage),
        channelID: String = context.getString(R.string.notificationChannel),
        notificationID: Int? = null,
    ): Notification {
        mChannelID = channelID

        if (notificationID != null)
            NOTIFICATION_ID = notificationID

        createNotificationChannel(context)

        val builder = NotificationCompat.Builder(context, mChannelID)
            .setContentText(message)
            .setContentTitle(title)
            .setOngoing(true)
            .setPriority(Notification.PRIORITY_HIGH)
            .setTicker(message)
            .setWhen(System.currentTimeMillis())

        // Set the Channel ID for Android O.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId(mChannelID) // Channel ID
        }
        return builder.build()
    }

    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                mChannelID,
                "Foreground Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )

            val manager =
                context.getSystemService(NotificationManager::class.java)

            manager.createNotificationChannel(serviceChannel)
        }
    }
}
