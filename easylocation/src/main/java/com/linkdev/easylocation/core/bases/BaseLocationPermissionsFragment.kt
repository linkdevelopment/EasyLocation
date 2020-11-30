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
package com.linkdev.easylocation.core.bases

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.LocationManager
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import com.linkdev.easylocation.core.models.LocationResultError

/**
 * * // Copyright (c) 2020 LinkDev. All rights reserved.**/
abstract class BaseLocationPermissionsFragment : Fragment() {

    companion object {
        private const val MY_PERMISSIONS_REQUEST_COARSE_LOCATION = 1
        private const val REQUEST_CODE_LOCATION_SETTINGS = 2000
    }

    /**
     * Called when both LocationPermission and locationSetting are granted.
     */
    abstract fun onLocationPermissionsReady()

    abstract fun onLocationPermissionError(locationResultError: LocationResultError)

    abstract fun showLocationPermissionRationalDialog()

    private fun onLocationPermissionGranted() {
        checkLocationSettings(activity)
    }

    private fun onLocationPermissionDenied() {
        onLocationPermissionError(LocationResultError.PermissionDenied())
    }

    private fun onLocationSettingGranted() {
        onLocationPermissionsReady()
    }

    private fun onLocationSettingDenied() {
        onLocationPermissionError(LocationResultError.SettingDenied())
    }

    //* Location Permission *//
    fun checkLocationPermissions(context: Context?) {
        when {
            shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) -> {
                // show dialog in case the user had already clicked deny before to redirect to settings
                showLocationPermissionRationalDialog()
            }
            checkLocationSelfPermission(context) -> {
                requestPermissions(
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    MY_PERMISSIONS_REQUEST_COARSE_LOCATION
                )
            }
            else -> {
                onLocationPermissionGranted()
            }
        }
    }

    private fun checkLocationSelfPermission(context: Context?): Boolean {
        return ActivityCompat.checkSelfPermission(
            context!!, Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == MY_PERMISSIONS_REQUEST_COARSE_LOCATION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                onLocationPermissionGranted()
            } else {
                onLocationPermissionDenied()
            }
        }
    }

    //* Location Setting *//
    private fun checkLocationSettings(context: Context?) {
        val locationRequest = LocationRequest.create()
        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        val task =
            LocationServices.getSettingsClient(context!!).checkLocationSettings(builder.build())
        task.addOnCompleteListener { task1: Task<LocationSettingsResponse?> ->
            try { // All location settings are satisfied. The client can initialize location requests here.
                task1.getResult(ApiException::class.java)
                onLocationSettingGranted()
            } catch (exception: ApiException) {
                when (exception.statusCode) {
                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED ->  // Location settings are not satisfied. But could be fixed by showing the user a dialog.
                        try {
                            val resolvable = exception as ResolvableApiException
                            requestLocationSetting(resolvable)
                        } catch (e: ClassCastException) {
                            e.printStackTrace()
                            onLocationSettingDenied()
                        }
                    LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> onLocationSettingDenied()
                }
            }
        }
    }

    private fun requestLocationSetting(resolvable: ResolvableApiException) {
        try {
            startIntentSenderForResult(
                resolvable.resolution.intentSender,
                REQUEST_CODE_LOCATION_SETTINGS, null, 0, 0, 0, null
            )
        } catch (e: IntentSender.SendIntentException) {
            e.printStackTrace()
            onLocationSettingDenied()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_LOCATION_SETTINGS) {
            if (resultCode == Activity.RESULT_OK) {
                if (!isLocationEnabled(activity!!)) {
                    onLocationSettingDenied()
                } else {
                    onLocationSettingGranted()
                }
            } else {
                onLocationSettingDenied()
            }
        }
    }

    private fun isLocationEnabled(context: Context): Boolean {
        val lm = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return try {
            lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
