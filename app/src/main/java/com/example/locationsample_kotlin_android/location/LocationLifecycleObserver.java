package com.example.locationsample_kotlin_android.location;

import android.content.Context;
import android.location.Location;
import android.os.Handler;
import androidx.annotation.Nullable;
import androidx.lifecycle.*;

public class LocationLifecycleObserver implements LifecycleObserver, LocationStatusListener {
    private Context mContext;

    private static final int DEFAULT_MAX_LOCATION_REQUEST_TIME = 15000;

    private boolean mSingleLocationRequest = false;
    private Handler mLocationRequestTimeoutHandler;

    private LocationProvidersContract mLocationProvidersContract;

    private MutableLiveData<LocationStatus> mLocationResponseLiveData;
    private long mMaxLocationRequestTime = DEFAULT_MAX_LOCATION_REQUEST_TIME;
    private boolean mIsFetchLatestKnownLocation = false;

    /**
     * @param singleLocationRequest should listen for the location change only oneTime.
     */
    public LocationLifecycleObserver(Context context, boolean singleLocationRequest) {
        this(context, null);
        mSingleLocationRequest = singleLocationRequest;
    }

    /**
     * @param context
     * @param maxLocationRequestTime Sets the max location updates request time
     *                               if exceeded stops the location updates and returns error
     *                               set to Null for default time {#LocationLifecycleObserver.DEFAULT_MAX_LOCATION_REQUEST_TIME}.
     */
    public LocationLifecycleObserver(Context context, @Nullable Long maxLocationRequestTime) {
        this.mContext = context;
        if (maxLocationRequestTime != null)
            mMaxLocationRequestTime = maxLocationRequestTime;
        mLocationResponseLiveData = new MutableLiveData<>();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    void onDestroy() {
        stopLocationUpdates();
    }

    /**
     * createsLocationRequest
     */
    public MutableLiveData<LocationStatus> startNetworkLocationUpdates() {
        startLocationRequestTimer();
        createNetworkLocationRequest();
        return mLocationResponseLiveData;
    }

    /**
     * Creates location updates
     *
     * @param interval
     * @param fastestInterval
     * @return
     */
    public MutableLiveData<LocationStatus> startFusedLocationUpdates(long interval, long fastestInterval) {
        startLocationRequestTimer();
        createFusedLocationRequest(interval, fastestInterval);
        return mLocationResponseLiveData;
    }

    private void createFusedLocationRequest(long interval, long fastestInterval) {
        mLocationProvidersContract = new LocationProviderFused(mContext, this, interval, fastestInterval);
        mLocationProvidersContract.requestLocationUpdates();
    }

    private void createNetworkLocationRequest() {
        mLocationProvidersContract = new LocationProviderNetwork(mContext, this);
        mLocationProvidersContract.requestLocationUpdates();
    }

    @Override
    public void onLocationRetrieved(Location location) {
        mLocationResponseLiveData.setValue(LocationStatus.success(location));
    }

    @Override
    public void onLocationRetrieveError(LocationStatus locationStatus) {
        mLocationResponseLiveData.setValue(locationStatus);
    }

    public void stopLocationUpdates() {
        if (mLocationProvidersContract != null)
            mLocationProvidersContract.stopLocationUpdates();

        if (mLocationRequestTimeoutHandler != null && runnable != null)
            mLocationRequestTimeoutHandler.removeCallbacks(runnable);
    }

    /**
     * Starts a timer to stop the request of the location updates after 30 seconds
     */
    private void startLocationRequestTimer() {
        mLocationRequestTimeoutHandler = new Handler();
        mLocationRequestTimeoutHandler.postDelayed(runnable, mMaxLocationRequestTime);
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            stopLocationUpdates();

            if (mIsFetchLatestKnownLocation) {
                mIsFetchLatestKnownLocation = false;
                mLocationResponseLiveData.setValue(LocationStatus.error());
            } else {
                mIsFetchLatestKnownLocation = true;
                startLocationRequestTimer();
                mLocationProvidersContract.fetchLatestKnownLocation();
            }
        }
    };
}
