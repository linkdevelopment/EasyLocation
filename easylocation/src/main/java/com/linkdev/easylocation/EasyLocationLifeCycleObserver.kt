package com.linkdev.easylocation

import android.content.Context
import android.location.Location
import android.os.Handler
import androidx.lifecycle.*
import com.linkdev.easylocation.location_providers.LocationOptions
import com.linkdev.easylocation.location_providers.LocationProvider
import com.linkdev.easylocation.location_providers.LocationProvidersTypes
import com.linkdev.easylocation.location_providers.LocationStatusListener
import com.linkdev.easylocation.location_providers.fused.DisplacementFusedLocationOptions
import com.linkdev.easylocation.location_providers.fused.TimeFusedLocationOptions
import com.linkdev.easylocation.location_providers.fused.FusedLocationProvider
import com.linkdev.easylocation.location_providers.gps.DisplacementLocationManagerOptions
import com.linkdev.easylocation.location_providers.gps.LocationManagerLocationProvider
import com.linkdev.easylocation.location_providers.gps.LocationManagerOptions

/**
 * Use this class to listen for location updates .
 *
 * @param lifecycle the lifecycle
 * @param mContext
 * @param mMaxLocationRequestTime Sets the max location updates request time
 *      if exceeded stops the location updates and returns error <br/>
 *      @Default [EasyLocationLifeCycleObserver.DEFAULT_MAX_LOCATION_REQUEST_TIME].
 *
 * @param singleLocationRequest true to emit the location only once.
 */
class EasyLocationLifeCycleObserver(lifecycle: Lifecycle, private val mContext: Context,
        private var mMaxLocationRequestTime: Long = EasyLocationConstants.DEFAULT_MAX_LOCATION_REQUEST_TIME,
        private var singleLocationRequest: Boolean = false
) : LifecycleObserver {

    private lateinit var mLocationProvider: LocationProvider
    private val mLocationResponseLiveData: MutableLiveData<LocationStatus> = MutableLiveData()
    private var mLocationRequestTimeoutHandler: Handler = Handler()

    init {
        lifecycle.addObserver(this)
    }

    /**
     * @param locationProviderType Represents the location provider used to retrieve the location one of [LocationProvidersTypes] enum values.
     *
     * @param locationOptions The specs required for retrieving location info, Depending on [locationProviderType]:
     * - [LocationProvidersTypes.GPS_LOCATION_PROVIDER] Should be:
     *      + [GPSLocationOptions]
     * - [LocationProvidersTypes.FUSED_LOCATION_PROVIDER] Should be one of:
     *      + [DisplacementFusedLocationOptions]
     *      + [TimeFusedLocationOptions]
     *
     * @return LiveData object to listen for location updates with [LocationStatus].
     *
     * @throws IllegalArgumentException If the [locationOptions] does not correspond to the selected [LocationProvidersTypes] mentioned above.
     */
    fun requestLocationUpdates(locationProviderType: LocationProvidersTypes, locationOptions: LocationOptions):
            LiveData<LocationStatus> {
        return when (locationProviderType) {
            LocationProvidersTypes.FUSED_LOCATION_PROVIDER -> {
                if (locationOptions !is DisplacementFusedLocationOptions) {
                    throw IllegalArgumentException("Fused location provider options not found should be [FusedLocationOptions]")
                }
                startFusedLocationUpdates(locationOptions)
            }
            LocationProvidersTypes.GPS_LOCATION_PROVIDER -> {
                if (locationOptions !is DisplacementLocationManagerOptions) {
                    throw IllegalArgumentException("GPS location provider options not found should be [GPSLocationOptions]")
                }
                startGPSLocationUpdates(locationOptions)
            }
        }
    }

    /**
     * Creates location updates request with the fused location provider [LocationProvidersTypes.FUSED_LOCATION_PROVIDER].
     *
     * @param fusedLocationOptions could be one of:
     *  + [DisplacementFusedLocationOptions]
     *  + [TimeFusedLocationOptions]
     *
     * @return LiveData object to listen for location updates with [LocationStatus].
     */
    private fun startFusedLocationUpdates(fusedLocationOptions: LocationOptions): LiveData<LocationStatus> {
        if (singleLocationRequest)
            startLocationRequestTimer()
        requestFusedLocationUpdates(fusedLocationOptions)
        return mLocationResponseLiveData
    }

    /**
     * Creates location updates request with the gps location provider [LocationProvidersTypes.FUSED_LOCATION_PROVIDER].
     *
     * @return LiveData object to listen for location updates with [LocationStatus].
     */
    private fun startGPSLocationUpdates(DisplacementLocationManagerOptions: DisplacementLocationManagerOptions): LiveData<LocationStatus> {
        if (singleLocationRequest)
            startLocationRequestTimer()
        requestGPSLocationUpdates(DisplacementLocationManagerOptions)
        return mLocationResponseLiveData
    }

    private fun requestFusedLocationUpdates(fusedLocationOptions: LocationOptions) {
        mLocationProvider = FusedLocationProvider(mContext, fusedLocationOptions)
        mLocationProvider.requestLocationUpdates(mLocationStatusListener)
    }

    private fun requestGPSLocationUpdates(locationManagerOptions: LocationManagerOptions) {
        mLocationProvider = LocationManagerLocationProvider(mContext, locationManagerOptions)
        mLocationProvider.requestLocationUpdates(mLocationStatusListener)
    }

    fun stopLocationUpdates() {
        if (::mLocationProvider.isInitialized)
            mLocationProvider.stopLocationUpdates()
        mLocationRequestTimeoutHandler.removeCallbacks(runnable)
    }

    private val mLocationStatusListener: LocationStatusListener = object : LocationStatusListener {
        override fun onLocationRetrieved(location: Location) {
            if (singleLocationRequest)
                stopLocationUpdates()
            mLocationResponseLiveData.value = LocationStatus.Success(location)
        }

        override fun onLocationRetrieveError(locationStatus: LocationStatus?) {
            if (singleLocationRequest)
                stopLocationUpdates()
            mLocationResponseLiveData.value = locationStatus
        }
    }

    /**
     * Starts a timer to stop the request of the location updates after [mMaxLocationRequestTime] seconds.
     */
    private fun startLocationRequestTimer() {
        mLocationRequestTimeoutHandler.postDelayed(runnable, mMaxLocationRequestTime)
    }

    private val runnable: Runnable = Runnable {
        stopLocationUpdates()
        mLocationProvider.fetchLatestKnownLocation()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy() {
        stopLocationUpdates()
    }
}
