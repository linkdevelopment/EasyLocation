package com.example.locationsample_kotlin_android.location

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentSender.SendIntentException
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.example.locationsample_kotlin_android.location.LocationHelper.isLocationEnabled
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task

/**
 * Created by Mohammed Fareed on 30/5/2019.
 */
abstract class BaseLocationFragment : Fragment() {

    /**
     * Called when both LocationPermission and locationSetting are granted.
     */
    abstract fun onLocationReady()

    abstract fun onLocationReadyError(locationError: LocationError)

    /**
     * Checks both location permission and settings are granted.
     */
    protected fun checkLocationReady() {
        checkLocationPermissions(activity)
    }

    protected fun onLocationPermissionGranted() {
        Log.d(TAG, "onLocationPermissionGranted: ")
        checkLocationSettings(activity)
    }

    protected fun onLocationPermissionDenied() {
        onLocationReadyError(LocationError.LOCATION_PERMISSION_DENIED)
    }

    private fun onLocationSettingGranted() {
        onLocationReady()
    }

    private fun onLocationSettingDenied() {
        onLocationReadyError(LocationError.LOCATION_SETTING_DENIED)
    }

    //* Location Permission *//
    private fun checkLocationPermissions(context: Context?) {
        if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) { // show dialog in case the user had already clicked deny before to redirect to settings
            onLocationReadyError(LocationError.SHOULD_SHOW_RATIONAL)
        } else if (ActivityCompat.checkSelfPermission(context!!, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    MY_PERMISSIONS_REQUEST_COARSE_LOCATION)
        } else {
            onLocationPermissionGranted()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == MY_PERMISSIONS_REQUEST_COARSE_LOCATION) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                onLocationPermissionGranted()
            } else {
                onLocationPermissionDenied()
            }
        }
    }

    //* Location Setting *//
    private fun checkLocationSettings(context: Context?) {
        val locationRequest = LocationRequest.create().apply {
            this.interval = Constants.DEFAULT_MAX_WAIT_TIME
            this.smallestDisplacement = Constants.DEFAULT_MIN_DISTANCE
            this.fastestInterval = Constants.DEFAULT_FASTEST_INTERVAL
            this.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        val task = LocationServices.getSettingsClient(context!!).checkLocationSettings(builder.build())
        task.addOnCompleteListener { task1: Task<LocationSettingsResponse?> ->
            try { // All location settings are satisfied. The client can initialize location requests here.
                task1.getResult(ApiException::class.java)
                onLocationSettingGranted()
            } catch (exception: ApiException) {
                when (exception.statusCode) {
                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED ->  // Location settings are not satisfied. But could be fixed by showing the user a dialog.
                        try {
                            val resolvable = exception as ResolvableApiException
                            requestLocationSetting(context, resolvable)
                        } catch (e: ClassCastException) {
                            e.printStackTrace()
                            onLocationSettingDenied()
                        }
                    LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> onLocationSettingDenied()
                }
            }
        }
    }

    private fun requestLocationSetting(context: Context?, resolvable: ResolvableApiException) {
        try {
            startIntentSenderForResult(resolvable.resolution.intentSender, REQUEST_CODE_LOCATION_SETTINGS, null, 0, 0, 0, null)
        } catch (e: SendIntentException) {
            e.printStackTrace()
            onLocationSettingDenied()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
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

    companion object {
        const val TAG = "BaseLocationFragment"
        private const val MY_PERMISSIONS_REQUEST_COARSE_LOCATION = 1
        private const val REQUEST_CODE_LOCATION_SETTINGS = 2000
    }
}