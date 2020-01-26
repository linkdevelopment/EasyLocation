package com.example.locationsample_kotlin_android.location

import android.content.Context
import android.location.Location
import android.os.Handler
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.OnLifecycleEvent
import com.example.locationsample_kotlin_android.location.location_providers.LocationOptions
import com.example.locationsample_kotlin_android.location.location_providers.LocationProviders
import com.example.locationsample_kotlin_android.location.location_providers.LocationProvidersTypes
import com.example.locationsample_kotlin_android.location.location_providers.LocationStatusListener
import com.example.locationsample_kotlin_android.location.location_providers.fused.FusedLocationOptions
import com.example.locationsample_kotlin_android.location.location_providers.fused.FusedLocationProvider
import com.example.locationsample_kotlin_android.location.location_providers.network.NetworkLocationOptions
import com.example.locationsample_kotlin_android.location.location_providers.network.NetworkLocationProvider

/**
 * todo
 * @param mContext
 * @param mMaxLocationRequestTime Sets the max location updates request time
 *      if exceeded stops the location updates and returns error <P>
 *      @Default{#LocationLifecycleObserver.DEFAULT_MAX_LOCATION_REQUEST_TIME}.
 * @param mIsSingleLocationRequest todo
 */
class LocationLifeCycleObserver(lifecycle: Lifecycle, private val mContext: Context,
                                private var mMaxLocationRequestTime: Long = Constants.DEFAULT_MAX_LOCATION_REQUEST_TIME,
                                private var mIsSingleLocationRequest: Boolean = false) : LifecycleObserver {

    private lateinit var mLocationProviders: LocationProviders
    private val mLocationResponseLiveData: MutableLiveData<LocationStatus> = MutableLiveData()
    private var mLocationRequestTimeoutHandler: Handler = Handler()

    init {
        lifecycle.addObserver(this)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy() {
        stopLocationUpdates()
    }

    fun requestLocationUpdates(locationProvidersTypes: LocationProvidersTypes, locationOptions: LocationOptions): MutableLiveData<LocationStatus> {
        return when (locationProvidersTypes) {
            LocationProvidersTypes.FUSED_LOCATION_PROVIDER -> {
                if (locationOptions !is FusedLocationOptions) {
                    throw Exception("Fused location provider options not found should be [FusedLocationOptions]")
                }
                startFusedLocationUpdates(locationOptions)
            }
            LocationProvidersTypes.NETWORK_LOCATION_PROVIDER -> {
                if (locationOptions !is NetworkLocationOptions) {
                    throw Exception("Network location provider options not found should be [NetworkLocationOptions]")
                }
                startNetworkLocationUpdates(locationOptions)
            }
        }
    }

    /**
     * creates Location Request.
     *
     * @return
     */
    private fun startNetworkLocationUpdates(networkLocationOptions: NetworkLocationOptions): MutableLiveData<LocationStatus> {
        if (mIsSingleLocationRequest)
            startLocationRequestTimer()
        requestNetworkLocationUpdates(networkLocationOptions)
        return mLocationResponseLiveData
    }

    /**
     * Creates location updates.
     *
     * @param fusedLocationOptions
     * @return
     */
    private fun startFusedLocationUpdates(fusedLocationOptions: FusedLocationOptions): MutableLiveData<LocationStatus> {
        if (mIsSingleLocationRequest)
            startLocationRequestTimer()
        requestFusedLocationUpdates(fusedLocationOptions)
        return mLocationResponseLiveData
    }

    private val mLocationStatusListener: LocationStatusListener = object : LocationStatusListener {
        override fun onLocationRetrieved(location: Location) {
            if (mIsSingleLocationRequest)
                stopLocationUpdates()
            mLocationResponseLiveData.value = LocationStatus.Success(location)
        }

        override fun onLocationRetrieveError(locationStatus: LocationStatus?) {
            if (mIsSingleLocationRequest)
                stopLocationUpdates()
            mLocationResponseLiveData.value = locationStatus
        }
    }

    private fun requestFusedLocationUpdates(fusedLocationOptions: FusedLocationOptions) {
        mLocationProviders = FusedLocationProvider(mContext, fusedLocationOptions)
        mLocationProviders.requestLocationUpdates(mLocationStatusListener)
    }

    private fun requestNetworkLocationUpdates(networkLocationOptions: NetworkLocationOptions) {
        mLocationProviders = NetworkLocationProvider(mContext, networkLocationOptions)
        mLocationProviders.requestLocationUpdates(mLocationStatusListener)
    }

    fun stopLocationUpdates() {
        if (::mLocationProviders.isInitialized)
            mLocationProviders.stopLocationUpdates()
        mLocationRequestTimeoutHandler.removeCallbacks(runnable)
    }

    /**
     * Starts a timer to stop the request of the location updates after [mMaxLocationRequestTime] seconds
     */
    private fun startLocationRequestTimer() {
        mLocationRequestTimeoutHandler.postDelayed(runnable, mMaxLocationRequestTime)
    }

    private val runnable: Runnable = Runnable {
        stopLocationUpdates()
        mLocationProviders.fetchLatestKnownLocation()
    }
}