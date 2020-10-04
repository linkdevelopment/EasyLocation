package com.linkdev.easylocation

import androidx.lifecycle.LiveData
import com.linkdev.easylocation.location_providers.LocationOptions
import com.linkdev.easylocation.location_providers.LocationProvidersTypes
import com.linkdev.easylocation.location_providers.LocationResult
import com.linkdev.easylocation.utils.EasyLocationConstants

// Created by Mohammed Fareed on 9/27/2020.
// Copyright (c) 2020 Link Development All rights reserved.
interface IEasyLocationObserver {

    fun requestLocationUpdates(
        locationProviderType: LocationProvidersTypes,
        locationOptions: LocationOptions
    ): LiveData<LocationResult>

    fun stopLocationUpdates()

    fun setMaxLocationRequestTime(maxLocationRequestTime: Long = EasyLocationConstants.DEFAULT_MAX_LOCATION_REQUEST_TIME)

    fun setSingleLocationRequest(singleLocationRequest: Boolean = false)
}
