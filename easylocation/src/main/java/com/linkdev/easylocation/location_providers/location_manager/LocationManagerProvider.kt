package com.linkdev.easylocation.location_providers.location_manager

import android.Manifest
import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import androidx.annotation.RequiresPermission
import com.linkdev.easylocation.location_providers.LocationResult
import com.linkdev.easylocation.location_providers.LocationProvider
import com.linkdev.easylocation.location_providers.LocationResultListener
import com.linkdev.easylocation.location_providers.location_manager.options.DisplacementLocationManagerOptions
import com.linkdev.easylocation.location_providers.location_manager.options.LocationManagerOptions
import com.linkdev.easylocation.location_providers.location_manager.options.LocationManagerProviderTypes
import com.linkdev.easylocation.location_providers.location_manager.options.TimeLocationManagerOptions
import com.linkdev.easylocation.utils.LocationUtils

/**
 * This Provider uses the LocationManager to retrieve location.
 *
 * For more info check [https://developer.android.com/reference/android/location/LocationManager]
 */
internal class LocationManagerProvider(
    private val mContext: Context,
    private val mLocationOptions: LocationManagerOptions
) : LocationProvider {

    private lateinit var mProvider: String
    private lateinit var mLocationResultListener: LocationResultListener
    private val mLocationManager =
        mContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    override fun requestLocationUpdates(locationResultListener: LocationResultListener) {
        mLocationResultListener = locationResultListener
        if (!LocationUtils.locationPermissionGranted(mContext)) {
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
        if (!LocationUtils.locationPermissionGranted(mContext)) {
            mLocationResultListener.onLocationRetrievalError(LocationResult.LocationPermissionNotGranted())
            return
        }

        val location = mLocationManager.getLastKnownLocation(mProvider)

        if (location != null)
            onLocationRetrieved(location)
        else
            mLocationResultListener.onLocationRetrievalError(LocationResult.Error())
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

    /**
     * Retrieves the LocationProviders from the Location manager based on the selected [LocationManagerProviderTypes].
     *
     * In case the type selected [LocationManagerProviderTypes.CRITERIA_BASED]
     * and there are no providers available matching this criteria GPS will be used.
     */
    private fun getProvider(): String {
        return when (mLocationOptions.locationManagerProvider) {
            LocationManagerProviderTypes.GPS -> LocationManager.GPS_PROVIDER
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
