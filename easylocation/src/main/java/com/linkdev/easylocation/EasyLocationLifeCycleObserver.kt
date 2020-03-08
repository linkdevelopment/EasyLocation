package com.linkdev.easylocation

import android.content.Context
import android.location.Location
import androidx.lifecycle.*
import com.linkdev.easylocation.location_providers.*
import com.linkdev.easylocation.location_providers.LocationResultListener

/**
 * Use this class to listen for location updates .
 *
 * @param lifecycle the lifecycle
 * @param mContext
 * @param mMaxLocationRequestTime Sets the max location updates request time
 *      if exceeded stops the location updates and returns error <br/>
 *      @Default [EasyLocationLifeCycleObserver.DEFAULT_MAX_LOCATION_REQUEST_TIME].
 *
 * @param mSingleLocationRequest true to emit the location only once.
 */
class EasyLocationLifeCycleObserver(lifecycle: Lifecycle, private val mContext: Context,
                                    private var mMaxLocationRequestTime: Long = EasyLocationConstants.DEFAULT_MAX_LOCATION_REQUEST_TIME,
                                    private var mSingleLocationRequest: Boolean = false
) : LifecycleObserver, LocationResultListener {

    private val mLocationResponseLiveData: MutableLiveData<LocationResult> = MutableLiveData()

    private var mEasyLocationProvidersFactory: EasyLocationProvidersFactory = EasyLocationProvidersFactory(mContext, this, mMaxLocationRequestTime, mSingleLocationRequest)

    init {
        lifecycle.addObserver(this)
    }

    /**
     * @param locationProviderType Represents the location provider used to retrieve the location one of [LocationProvidersTypes] enum values.
     *
     * @param locationOptions The specs required for retrieving location info, Depending on [locationProviderType]:
     * - [LocationProvidersTypes.LOCATION_MANAGER_LOCATION_PROVIDER] Should be one of:
     *      + [DisplacementLocationManagerOptions]
     *      + [TimeLocationManagerOptions]
     * - [LocationProvidersTypes.FUSED_LOCATION_PROVIDER] Should be one of:
     *      + [DisplacementFusedLocationOptions]
     *      + [TimeFusedLocationOptions]
     *
     * @return LiveData object to listen for location updates with [LocationResult].
     *
     * @throws IllegalArgumentException If the [locationOptions] does not correspond to the selected [LocationProvidersTypes] mentioned above.
     */
    fun requestLocationUpdates(locationProviderType: LocationProvidersTypes, locationOptions: LocationOptions):
            LiveData<LocationResult> {
        mEasyLocationProvidersFactory.requestLocationUpdates(locationProviderType, locationOptions)

        return mLocationResponseLiveData
    }

    fun stopLocationUpdates() {
        mEasyLocationProvidersFactory.stopLocationUpdates()
    }

    override fun onLocationRetrieved(location: Location) {
        if (mSingleLocationRequest)
            stopLocationUpdates()
        mLocationResponseLiveData.value = LocationResult.Success(location)
    }

    override fun onLocationRetrieveError(locationResult: LocationResult?) {
        if (mSingleLocationRequest)
            stopLocationUpdates()
        mLocationResponseLiveData.value = locationResult
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy() {
        stopLocationUpdates()
    }
}
