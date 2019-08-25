package com.example.locationsample_kotlin_android.location;

import android.content.Context;
import android.location.Location;
import android.os.Handler;
import androidx.annotation.Nullable;
import androidx.lifecycle.*;

public class LocationLifecycleObserver implements LifecycleObserver, LocationStatusListener {
    private Context mContext;

    private static final int DEFAULT_MAX_LOCATION_REQUEST_TIME = 15000;

    private boolean mIsSingleLocationRequest = false;
    private Handler mLocationRequestTimeoutHandler;

    private LocationProvidersContract mLocationProvidersContract;

    private MutableLiveData<LocationStatus> mLocationResponseLiveData;
    private long mMaxLocationRequestTime = DEFAULT_MAX_LOCATION_REQUEST_TIME;

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

    public LocationLifecycleObserver singleLocationRequest() {
        mIsSingleLocationRequest = true;
        return this;
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    void onDestroy() {
        stopLocationUpdates();
    }

    /**
     * creates Location Request
     *
     * @return
     */
    public MutableLiveData<LocationStatus> startNetworkLocationUpdates() {
        if (mIsSingleLocationRequest)
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
        if (mIsSingleLocationRequest)
            startLocationRequestTimer();
        createFusedLocationRequest(interval, fastestInterval);
        return mLocationResponseLiveData;
    }

    @Override
    public void onLocationRetrieved(Location location) {
        if (mIsSingleLocationRequest)
            stopLocationUpdates();
        mLocationResponseLiveData.setValue(LocationStatus.success(location));
    }

    @Override
    public void onLocationRetrieveError(LocationStatus locationStatus) {
        if (mIsSingleLocationRequest)
            stopLocationUpdates();
        mLocationResponseLiveData.setValue(locationStatus);
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

            mLocationProvidersContract.fetchLatestKnownLocation();
        }
    };

    public void setSingleLocationRequest(boolean isSingleLocationRequest) {
        mIsSingleLocationRequest = isSingleLocationRequest;
    }

    private void createFusedLocationRequest(long interval, long fastestInterval) {
        mLocationProvidersContract = new LocationProviderFused(mContext, this, interval, fastestInterval);
        mLocationProvidersContract.requestLocationUpdates();
    }

    private void createNetworkLocationRequest() {
        mLocationProvidersContract = new LocationProviderNetwork(mContext, this);
        mLocationProvidersContract.requestLocationUpdates();
    }

    public void stopLocationUpdates() {
        if (mLocationProvidersContract != null)
            mLocationProvidersContract.stopLocationUpdates();

        if (mLocationRequestTimeoutHandler != null && runnable != null)
            mLocationRequestTimeoutHandler.removeCallbacks(runnable);
    }
}
