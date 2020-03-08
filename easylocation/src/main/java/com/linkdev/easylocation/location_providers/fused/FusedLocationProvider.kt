package com.linkdev.easylocation.location_providers.fused

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Criteria
import android.location.Location
import android.location.LocationManager
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*
import com.linkdev.easylocation.location_providers.LocationResult
import com.linkdev.easylocation.location_providers.LocationOptions
import com.linkdev.easylocation.location_providers.LocationProvider
import com.linkdev.easylocation.location_providers.LocationResultListener

/**
 * This Provider uses the FusedLocationProvider and Google Play Services to retrieve location.
 *
 * For more info check [https://developers.google.com/location-context/fused-location-provider]
 */
internal class FusedLocationProvider(private val mContext: Context, private val mFusedLocationOptions: LocationOptions) : LocationProvider {

    private lateinit var mLocationResultListener: LocationResultListener
    private val mFusedLocationClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(mContext)

    override fun requestLocationUpdates(locationResultListener: LocationResultListener) {
        mLocationResultListener = locationResultListener

        val locationRequest = createLocationRequest(mFusedLocationOptions)
        mFusedLocationClient.requestLocationUpdates(locationRequest, mLocationCallback, null)
    }

    override fun stopLocationUpdates() {
        mFusedLocationClient.removeLocationUpdates(mLocationCallback)
    }

    override fun fetchLatestKnownLocation() {
        val locationManager = mContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            val bestProvider = locationManager.getBestProvider(Criteria(), true)
            if (bestProvider.isNullOrBlank()) {
                mLocationResultListener.onLocationRetrieveError(LocationResult.Error())
                return
            }

            val location = locationManager.getLastKnownLocation(bestProvider)
            if (location != null)
                onLocationRetrieved(location)
            else mLocationResultListener.onLocationRetrieveError(LocationResult.Error())
        } else {
            mLocationResultListener.onLocationRetrieveError(LocationResult.LocationPermissionNotGranted())
        }
    }

    private fun onLocationRetrieved(location: Location) {
        mLocationResultListener.onLocationRetrieved(location)
    }

    private val mLocationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: com.google.android.gms.location.LocationResult?) {
            if (locationResult == null) {
                mLocationResultListener.onLocationRetrieveError(LocationResult.Error())
                return
            }
            onLocationRetrieved(locationResult.lastLocation)
        }
    }

    private fun createLocationRequest(locationOptions: LocationOptions): LocationRequest {
        return when (locationOptions) {
            is DisplacementFusedLocationOptions ->
                LocationRequest.create().apply {
                    this.smallestDisplacement = locationOptions.smallestDisplacement
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
