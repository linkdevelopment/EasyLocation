package com.linkdev.easylocation.location_providers.gps

import android.location.Criteria
import com.linkdev.easylocation.EasyLocationConstants
import com.linkdev.easylocation.location_providers.LocationOptions

/**
 * EasyLocation_Android Created by Mohammed.Fareed on 1/15/2020.
 * * // Copyright (c) 2020 LinkDev. All rights reserved.**/
/**
 *  Options for using [LocationManagerLocationProvider]
 *
 * @param minTime min time interval for every location update in milliSeconds Default 5 Seconds.
 * @param locationManagerProvider One of [LocationManagerProviderTypes] , There are multiple sensors in the device
 * to determine device location using the Location manger framework you can specify which specific provider to use.
 * @param criteria Should be applied in case of [LocationManagerProviderTypes.CRITERIA_BASED] only otherwise the value will be ignored.
 */
class TimeLocationManagerOptions(val minTime: Long = EasyLocationConstants.DEFAULT_FASTEST_INTERVAL,
                                 override val locationManagerProvider: LocationManagerProviderTypes = LocationManagerProviderTypes.GPS,
                                 override val criteria: Criteria? = null) :
        LocationManagerOptions(locationManagerProvider, criteria)
