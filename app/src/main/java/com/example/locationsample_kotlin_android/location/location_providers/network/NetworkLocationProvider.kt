package com.example.locationsample_kotlin_android.location.location_providers.network

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.example.locationsample_kotlin_android.location.LocationStatus
import com.example.locationsample_kotlin_android.location.location_providers.LocationProviders
import com.example.locationsample_kotlin_android.location.location_providers.LocationStatusListener
import com.google.android.gms.location.LocationRequest

internal class NetworkLocationProvider(private val mContext: Context,
                                       private val mLocationStatusListener: LocationStatusListener,
                                       private val minTime: Long, private val minDistance: Float) : LocationProviders {

    private val mLocationManager = mContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    override fun requestLocationUpdates() {
        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            mLocationStatusListener.onLocationRetrieveError(LocationStatus.LocationPermissionNotGranted())
            return
        }

        mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, minTime, minDistance, mLocationListener)
    }

    override fun stopLocationUpdates() {
        mLocationManager.removeUpdates(mLocationListener)
    }

    override fun fetchLatestKnownLocation() {
        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            val location = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)

            if (location != null)
                onLocationRetrieved(location)
            else
                mLocationStatusListener.onLocationRetrieveError(LocationStatus.Error())
        } else {
            mLocationStatusListener.onLocationRetrieveError(LocationStatus.LocationPermissionNotGranted())
        }
    }

    private fun onLocationRetrieved(location: Location) {
        mLocationStatusListener.onLocationRetrieved(location)
    }

    private val mLocationListener: LocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            onLocationRetrieved(location)
        }

        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
        override fun onProviderEnabled(provider: String) {}
        override fun onProviderDisabled(provider: String) {}
    }
}