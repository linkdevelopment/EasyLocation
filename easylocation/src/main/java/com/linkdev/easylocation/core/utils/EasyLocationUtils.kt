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

import android.Manifest
import android.content.Context
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Build
import android.provider.Settings
import android.provider.Settings.SettingNotFoundException
import android.text.TextUtils
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import kotlin.reflect.KFunction2

/**
 * Reusable utilities for EasyLocation.
 */
internal object EasyLocationUtils {

    fun showBasicDialog(
        context: Context?, title: String?, message: String?,
        positiveButton: String?, negativeButton: String?,
        onDialogInteraction: KFunction2<@ParameterName(name = "dialogInterface") DialogInterface, @ParameterName(
            name = "which"
        ) Int, Unit>
    ): AlertDialog {
        return AlertDialog.Builder(context!!)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(positiveButton, onDialogInteraction)
            .setNegativeButton(negativeButton, onDialogInteraction)
            .show()
    }

    /**
     * Checks for the location permission is grated for the given [context]
     *
     * @param context the context to check the location for
     */
    fun isLocationPermissionGranted(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(
                    context, Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
    }

    fun checkLocationSettings(context: Context): Boolean {
        return try {
            val locationMode =
                Settings.Secure.getInt(context.contentResolver, Settings.Secure.LOCATION_MODE)

            locationMode != Settings.Secure.LOCATION_MODE_OFF
        } catch (e: SettingNotFoundException) {
            e.printStackTrace()
            false
        }
    }
}
