package com.linkdev.easylocation.location_providers.fused

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.os.Looper
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.Task
import com.linkdev.easylocation.location_providers.LocationOptions
import com.linkdev.easylocation.location_providers.LocationProvider
import com.linkdev.easylocation.location_providers.LocationResult
import com.linkdev.easylocation.location_providers.LocationResultListener
import com.linkdev.easylocation.location_providers.fused.options.DisplacementFusedLocationOptions
import com.linkdev.easylocation.location_providers.fused.options.FusedLocationOptions
import com.linkdev.easylocation.location_providers.fused.options.TimeFusedLocationOptions
import com.linkdev.easylocation.utils.LocationUtils

/**
 * This Provider uses the FusedLocationProvider and Google Play Services to retrieve location.
 *
 * For more info check [https://developers.google.com/location-context/fused-location-provider]
 */
internal class FusedLocationProvider(
    private val mContext: Context,
    private val mFusedLocationOptions: FusedLocationOptions
) : LocationProvider {

    private lateinit var mLocationResultListener: LocationResultListener
    private val mFusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(mContext)

    @SuppressLint("MissingPermission")
    override fun requestLocationUpdates(locationResultListener: LocationResultListener) {
        if (!LocationUtils.locationPermissionGranted(mContext)) {
            mLocationResultListener.onLocationRetrievalError(LocationResult.LocationPermissionNotGranted())
            return
        }
        mLocationResultListener = locationResultListener

        val locationRequest = createLocationRequest(mFusedLocationOptions)

        mFusedLocationClient.requestLocationUpdates(locationRequest, mLocationCallback, Looper.getMainLooper())
    }

    override fun stopLocationUpdates() {
        mFusedLocationClient.removeLocationUpdates(mLocationCallback)
    }

    @SuppressLint("MissingPermission")
    override fun fetchLatestKnownLocation() {
        if (!LocationUtils.locationPermissionGranted(mContext)) {
            mLocationResultListener.onLocationRetrievalError(LocationResult.LocationPermissionNotGranted())
            return
        }

        mFusedLocationClient.lastLocation.addOnCompleteListener { onLastLocationRetrieved(it) }
    }

    private fun onLocationRetrieved(location: Location) {
        mLocationResultListener.onLocationRetrieved(location)
    }

    private val mLocationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: com.google.android.gms.location.LocationResult?) {
            if (locationResult == null) {
                mLocationResultListener.onLocationRetrievalError(LocationResult.Error())
                return
            }
            locationResult.locations.forEach {
                onLocationRetrieved(it)
            }
        }
    }

    private fun onLastLocationRetrieved(locationTask: Task<Location>) {
        if (locationTask.isSuccessful)
            mLocationResultListener.onLocationRetrieved(locationTask.result)
        else
            mLocationResultListener.onLocationRetrievalError(LocationResult.Error())
    }

    private fun createLocationRequest(locationOptions: LocationOptions): LocationRequest {
        return when (locationOptions) {
            is DisplacementFusedLocationOptions ->
                LocationRequest.create().apply {
                    this.smallestDisplacement = locationOptions.smallestDisplacement
                    this.interval = locationOptions.fastestInterval
                    this.fastestInterval = locationOptions.fastestInterval
                    this.priority = locationOptions.priority
                }
            is TimeFusedLocationOptions ->
                LocationRequest.create().apply {
                    this.interval = locationOptions.interval
                    this.fastestInterval = locationOptions.fastestInterval
                    this.priority = locationOptions.priority
                }
            else -> throw Exception("mFusedLocationOptions should be one of [DisplacementFusedLocationOptions, TimeFusedLocationOptions]")
        }
    }
}
