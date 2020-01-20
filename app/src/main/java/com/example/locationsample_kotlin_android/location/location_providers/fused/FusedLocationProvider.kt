package com.example.locationsample_kotlin_android.location.location_providers.fused

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Criteria
import android.location.Location
import android.location.LocationManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.locationsample_kotlin_android.location.LocationStatus
import com.example.locationsample_kotlin_android.location.location_providers.LocationProviders
import com.example.locationsample_kotlin_android.location.location_providers.LocationStatusListener
import com.google.android.gms.location.*

internal class FusedLocationProvider(private val mContext: Context,
                                     private val mLocationStatusListener: LocationStatusListener,
                                     private val mInterval: Long, private val mSmallestDisplacement: Float) : LocationProviders {

    private val mFusedLocationClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(mContext)

    override fun requestLocationUpdates() {
        val locationRequest = createLocationRequest(mInterval, mSmallestDisplacement)
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return
        }
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
                mLocationStatusListener.onLocationRetrieveError(LocationStatus.Error())
                return
            }

            val location = locationManager.getLastKnownLocation(bestProvider)
            if (location != null)
                onLocationRetrieved(location)
            else mLocationStatusListener.onLocationRetrieveError(LocationStatus.Error())
        } else {
            mLocationStatusListener.onLocationRetrieveError(LocationStatus.LocationPermissionNotGranted())
        }
    }

    private fun onLocationRetrieved(location: Location) {
        mLocationStatusListener.onLocationRetrieved(location)
    }

    private val mLocationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult?) {
            if (locationResult == null) {
                mLocationStatusListener.onLocationRetrieveError(LocationStatus.Error())
                return
            }
            onLocationRetrieved(locationResult.lastLocation)
        }
    }

    private fun createLocationRequest(interval: Long, smallestDisplacement: Float): LocationRequest {
        return LocationRequest.create().apply {
            this.interval = interval
            this.smallestDisplacement = smallestDisplacement
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
    }
}