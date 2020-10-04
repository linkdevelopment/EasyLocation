package com.linkdev.easylocation

import android.content.Context
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LiveData
import com.linkdev.easylocation.location_providers.LocationOptions
import com.linkdev.easylocation.location_providers.LocationProvidersTypes
import com.linkdev.easylocation.location_providers.LocationResult
import com.linkdev.easylocation.utils.EasyLocationConstants

// Created by Mohammed Fareed on 9/27/2020.
// Copyright (c) 2020 Link Development All rights reserved.
class EasyLocation private constructor(
    private val mContext: Context,
    private val mMaxLocationRequestTime: Long,
    private val mSingleLocationRequest: Boolean
) {

    private var mLocationObserver: IEasyLocationObserver? = null

    fun requestLifecycleLocationUpdates(
        lifecycle: Lifecycle,
        locationProviderType: LocationProvidersTypes, locationOptions: LocationOptions
    ): LiveData<LocationResult> {
        mLocationObserver = EasyLocationLifeCycleObserver(lifecycle, mContext)
        mLocationObserver?.setMaxLocationRequestTime(mMaxLocationRequestTime)
        mLocationObserver?.setSingleLocationRequest(mSingleLocationRequest)

        return mLocationObserver?.requestLocationUpdates(locationProviderType, locationOptions)!!
    }

    fun stopLocationUpdates() {
        mLocationObserver?.stopLocationUpdates()
    }

    // TODO: Let's talk about removing this Builder
    class Builder(private val context: Context) {
        private var mMaxLocationRequestTime: Long =
            EasyLocationConstants.DEFAULT_MAX_LOCATION_REQUEST_TIME

        private var mSingleLocationRequest: Boolean =
            EasyLocationConstants.DEFAULT_SINGLE_LOCATION_REQUEST

        fun setMaxLocationRequestTime(maxLocationRequestTime: Long): Builder {
            mMaxLocationRequestTime = maxLocationRequestTime
            return this
        }

        fun setSingleLocationRequest(singleLocationRequest: Boolean): Builder {
            mSingleLocationRequest = singleLocationRequest
            return this
        }

        fun build(): EasyLocation {
            return EasyLocation(context, mMaxLocationRequestTime, mSingleLocationRequest)
        }
    }
}
