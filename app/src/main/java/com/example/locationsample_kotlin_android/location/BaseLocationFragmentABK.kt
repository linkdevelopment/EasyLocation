package com.example.locationsample_kotlin_android.location

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.location.Location
import android.util.Log
import androidx.annotation.CallSuper
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsStatusCodes

/**
 * Created by Mohammed Fareed on 30/5/2019.
 */
abstract class BaseLocationFragmentABK : Fragment() {
//
//    private lateinit var mLocationLifecycleObserver: LocationLifecycleObserver
//
//    companion object {
//        const val TAG = "BaseLocationFragment"
//
//        private const val REQUEST_CODE_LOCATION_SETTINGS = 2000
//    }
//
//    /**
//     * Called location is retrieved successfully.
//     */
//    abstract fun onLocationRetrieved(location: Location)
//
//    /**
//     * Called location could not be retrieved.
//     */
//    abstract fun onLocationRetrievalError()
//
//    abstract fun onLocationSettingDenied()
//
//    /**
//     * Checks both location permission and settings are granted.
//     */
//    protected fun getLocation(customDialogMessage: String = "") {
//        checkLocationPermissions(mContext, customDialogMessage)
//    }
//
//    @CallSuper
//    override fun onPermissionGranted(permission: String) {
//        Log.d(TAG, "onLocationPermissionGranted")
//        if (permission == Manifest.permission.ACCESS_FINE_LOCATION)
//            checkLocationSettings()
//    }
//
//    private fun onLocationSettingGranted() {
//        mLocationLifecycleObserver = LocationLifecycleObserver(mContext,
//            LocationHelper.Constants.MAX_LOCATION_REQUEST_TIME, true)
//        lifecycle.addObserver(mLocationLifecycleObserver)
//
////        mLocationLifecycleObserver.startNetworkLocationUpdates()
//        mLocationLifecycleObserver.startFusedLocationUpdates(
//            LocationHelper.Constants.INTERVAL,
//            LocationHelper.Constants.FASTEST_INTERVAL
//        )
//            .observe(this, Observer { onLocationRetrieved(it) })
//    }
//
//    //* Location Permission *//
//    private fun checkLocationPermissions(context: Context, customDialogMessage: String) {
//        checkPermissions(
//            context, message = customDialogMessage,
//            permissions = *arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
//        )
//    }
//
//    //* Location Setting *//
//    private fun checkLocationSettings() {
//        val locationRequest = LocationHelper.createLocationRequest(
//            LocationHelper.Constants.INTERVAL,
//            LocationHelper.Constants.FASTEST_INTERVAL
//        )
//        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
//        val task =
//            LocationServices.getSettingsClient(mContext).checkLocationSettings(builder.build())
//        task.addOnCompleteListener { task1 ->
//            try {
//                // All location settings are satisfied. The client can initialize location requests here.
//                task1.getResult(ApiException::class.java)
//                onLocationSettingGranted()
//            } catch (exception: ApiException) {
//                when (exception.statusCode) {
//                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED ->
//                        // Location settings are not satisfied. But could be fixed by showing the user a dialog.
//                        try {
//                            val resolvable = exception as ResolvableApiException
//                            requestLocationSetting(context, resolvable)
//                        } catch (e: ClassCastException) {
//                            e.printStackTrace()
//                            onLocationSettingDenied()
//                        }
//
//                    LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> onLocationSettingDenied()
//                }
//            }
//        }
//    }
//
//    private fun requestLocationSetting(context: Context?, resolvable: ResolvableApiException) {
//        try {
//            startIntentSenderForResult(
//                resolvable.resolution.intentSender,
//                REQUEST_CODE_LOCATION_SETTINGS,
//                null,
//                0,
//                0,
//                0,
//                null
//            )
//        } catch (e: IntentSender.SendIntentException) {
//            e.printStackTrace()
//            onLocationSettingDenied()
//        }
//
//    }
//
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (requestCode == REQUEST_CODE_LOCATION_SETTINGS) {
//            if (resultCode == RESULT_OK) {
//                if (!LocationHelper.isLocationEnabled(mContext)) {
//                    onLocationSettingDenied()
//                } else {
//                    onLocationSettingGranted()
//                }
//            } else {
//                onLocationSettingDenied()
//            }
//        }
//    }
//
//    private fun onLocationRetrieved(locationStatus: LocationStatus) {
//        when (locationStatus.status) {
//            LocationStatus.Status.SUCCESS -> onLocationRetrieved(locationStatus.location)
//            LocationStatus.Status.ERROR -> onLocationRetrievalError()
//            LocationStatus.Status.PERMISSION_NOT_GRANTED -> onPermissionDenied(Manifest.permission.ACCESS_FINE_LOCATION)
//        }
//    }

}
