package com.linkdev.easylocation.location_providers.fused

import com.linkdev.easylocation.EasyLocationConstants
import com.linkdev.easylocation.location_providers.LocationOptions

/**
 * EasyLocation_Android Created by Mohammed.Fareed on 1/15/2020.
 * * // Copyright (c) 2020 LinkDev. All rights reserved.**/

/**
 *  Options for using [FusedLocationProvider]
 *
 * @param interval Desired interval for every location update in milliSeconds<p> @defaults_to 3 Seconds.
 * @param priority Get the quality of the request @defaults_to LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY.
 * @param fastestInterval Get the fastest interval of this request, in milliseconds, The system will never provide
 *                  location updates faster than it.<p> @defaults_to 2 milliseconds.
 */
class TimeFusedLocationOptions(val interval: Long = EasyLocationConstants.DEFAULT_INTERVAL,
                               val priority: Int = EasyLocationConstants.DEFAULT_PRIORITY,
                               val fastestInterval: Long = EasyLocationConstants.DEFAULT_FASTEST_INTERVAL) : LocationOptions
