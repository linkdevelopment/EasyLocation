package com.linkdev.easylocation.location_providers.location_manager

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import androidx.annotation.RequiresPermission
import androidx.core.content.ContextCompat
import com.linkdev.easylocation.location_providers.LocationResult
import com.linkdev.easylocation.location_providers.LocationProvider
import com.linkdev.easylocation.location_providers.LocationResultListener

/**
 * This Provider uses the LocationManager to retrieve location.
 *
 * For more info check [https://developer.android.com/reference/android/location/LocationManager]
 */
internal class LocationManagerProvider(private val mContext: Context,
                                       private val mLocationOptions: LocationManagerOptions) : LocationProvider {

    private lateinit var mProvider: String
    private lateinit var mLocationResultListener: LocationResultListener
    private val mLocationManager = mContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    override fun requestLocationUpdates(locationResultListener: LocationResultListener) {
        mLocationResultListener = locationResultListener
        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            mLocationResultListener.onLocationRetrievalError(LocationResult.LocationPermissionNotGranted())
            return
        }

        requestLocationUpdates()
    }

    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION])
    private fun requestLocationUpdates() {
        val minDistance = getMinDistance()
        val minTime = getMinTime()
        mProvider = getProvider()

        mLocationManager.requestLocationUpdates(mProvider, minTime, minDistance, mLocationListener)
    }

    override fun stopLocationUpdates() {
        mLocationManager.removeUpdates(mLocationListener)
    }

    override fun fetchLatestKnownLocation() {
        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            val location = mLocationManager.getLastKnownLocation(mProvider)

            if (location != null)
                onLocationRetrieved(location)
            else
                mLocationResultListener.onLocationRetrievalError(LocationResult.Error())
        } else {
            mLocationResultListener.onLocationRetrievalError(LocationResult.LocationPermissionNotGranted())
        }
    }

    private fun onLocationRetrieved(location: Location) {
        mLocationResultListener.onLocationRetrieved(location)
    }

    private fun getMinDistance(): Float {
        return if (mLocationOptions is DisplacementLocationManagerOptions)
            mLocationOptions.minDistance
        else 0f
    }

    private fun getMinTime(): Long {
        return if (mLocationOptions is TimeLocationManagerOptions)
            mLocationOptions.minTime
        else 0
    }

    private fun getProvider(): String {
        return when (mLocationOptions.locationManagerProvider) {
            LocationManagerProviderTypes.LOCATION_MANAGER -> LocationManager.GPS_PROVIDER
            LocationManagerProviderTypes.NETWORK -> LocationManager.NETWORK_PROVIDER
            LocationManagerProviderTypes.CRITERIA_BASED -> {
                if (mLocationOptions.criteria == null)
                    throw IllegalArgumentException("Criteria is not set on CRITERIA_BASED provider.")

                mLocationManager.getBestProvider(mLocationOptions.criteria!!, true)
                        ?: LocationManager.GPS_PROVIDER
            }
        }
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

enum class LocationManagerProviderTypes {
    LOCATION_MANAGER,
    NETWORK,
    CRITERIA_BASED
}
