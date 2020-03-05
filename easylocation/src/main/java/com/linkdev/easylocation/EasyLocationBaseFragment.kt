package com.linkdev.easylocation

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentSender.SendIntentException
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import com.linkdev.easylocation.location_providers.LocationOptions
import com.linkdev.easylocation.location_providers.LocationProvidersTypes
import kotlin.properties.Delegates

/**
 * Created by Mohammed Fareed on 30/5/2019.
 */
abstract class EasyLocationBaseFragment : Fragment() {

    private lateinit var mLocationProviderType: LocationProvidersTypes
    private lateinit var mLocationOptions: LocationOptions
    private var mSingleLocationRequest by Delegates.notNull<Boolean>()
    private var mMaxLocationRequestTime by Delegates.notNull<Long>()

    companion object {
        private const val MY_PERMISSIONS_REQUEST_COARSE_LOCATION = 1
        private const val REQUEST_CODE_LOCATION_SETTINGS = 2000
    }

    /**
     * Called when both LocationPermission and locationSetting are granted.
     */
    abstract fun onLocationRetrieved(location: Location)

    abstract fun onLocationRetrievalError(locationError: LocationError)

    /**
     * The entry point for [EasyLocationBaseFragment] after calling this method:
     *
     * 1- Location permission will be Checked.
     *
     * 2- Location setting will be Checked.
     *
     * 3- Finally, if previous points are valid will Use [EasyLocationLifeCycleObserver] to retrieve the location
     * and call the [onLocationRetrieved] callback method in case of successful retrieval,
     * Otherwise in case of errors [onLocationRetrievalError] will be called.
     *
     * @param locationProviderType Represents the location provider used to retrieve the location one of [LocationProvidersTypes] enum values.
     * @param locationOptions The specs required for retrieving location info, Depending on [locationProviderType]:
     * - [LocationProvidersTypes.GPS_LOCATION_PROVIDER] Should be:
     *      + [GPSLocationOptions]
     * - [LocationProvidersTypes.FUSED_LOCATION_PROVIDER] Should be one of:
     *      + [DisplacementFusedLocationOptions]
     *      + [TimeFusedLocationOptions]
     * @param singleLocationRequest true to emit the location only once.
     * @param maxLocationRequestTime Sets the max location updates request time
     *      if exceeded stops the location updates and returns error <P>
     *      @Default{#LocationLifecycleObserver.DEFAULT_MAX_LOCATION_REQUEST_TIME}.
     */
    protected fun checkLocationReady(locationProviderType: LocationProvidersTypes,
                                     locationOptions: LocationOptions,
                                     singleLocationRequest: Boolean = false,
                                     maxLocationRequestTime: Long = EasyLocationConstants.DEFAULT_MAX_LOCATION_REQUEST_TIME) {
        mLocationProviderType = locationProviderType
        mLocationOptions = locationOptions
        mSingleLocationRequest = singleLocationRequest
        mMaxLocationRequestTime = maxLocationRequestTime

        checkLocationPermissions(activity)
    }

    private fun onLocationPermissionGranted() {
        checkLocationSettings(activity)
    }

    protected fun onLocationPermissionDenied() {
        onLocationRetrievalError(LocationError.LOCATION_PERMISSION_DENIED)
    }

    private fun onLocationSettingGranted() {
        retrieveLocation()
    }

    private fun onLocationSettingDenied() {
        onLocationRetrievalError(LocationError.LOCATION_SETTING_DENIED)
    }

    private fun retrieveLocation() {
        EasyLocationLifeCycleObserver(lifecycle, context!!, mMaxLocationRequestTime, mSingleLocationRequest)
                .requestLocationUpdates(mLocationProviderType, mLocationOptions)
                .observe(this, Observer { locationStatus -> onLocationStatusRetrieved(locationStatus) })
    }

    private fun onLocationStatusRetrieved(locationStatus: LocationStatus) {
        when (locationStatus.status) {
            Status.SUCCESS -> {
                if (locationStatus.location == null) {
                    onLocationRetrievalError(LocationError.LOCATION_ERROR)
                    return
                }
                onLocationRetrieved(locationStatus.location)
            }
            Status.ERROR ->
                onLocationRetrievalError(LocationError.LOCATION_ERROR)
            Status.PERMISSION_NOT_GRANTED -> {
                onLocationRetrievalError(LocationError.LOCATION_PERMISSION_DENIED)
            }
        }
    }

    //* Location Permission *//
    private fun checkLocationPermissions(context: Context?) {
        when {
            shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) -> { // show dialog in case the user had already clicked deny before to redirect to settings
                onLocationRetrievalError(LocationError.SHOULD_SHOW_RATIONAL)
            }
            ActivityCompat.checkSelfPermission(context!!, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED -> {
                requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                        MY_PERMISSIONS_REQUEST_COARSE_LOCATION
                )
            }
            else -> {
                onLocationPermissionGranted()
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
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
            startIntentSenderForResult(resolvable.resolution.intentSender, REQUEST_CODE_LOCATION_SETTINGS, null, 0, 0, 0, null)
        } catch (e: SendIntentException) {
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
