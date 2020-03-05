package com.linkdev.easylocation.location_providers.fused

import com.linkdev.easylocation.EasyLocationConstants
import com.linkdev.easylocation.location_providers.LocationOptions

/**
 * EasyLocation_Android Created by Mohammed.Fareed on 1/15/2020.
 * * // Copyright (c) 2020 LinkDev. All rights reserved.**/

/**
 *  Options for using [FusedLocationProvider]
 *
 * @param smallestDisplacement Get the minimum distance between location updates in meters<p> @defaults_to 10 meters
 * @param priority Get the quality of the request @defaults_to LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
 * @param fastestInterval Get the fastest interval of this request, in milliseconds, The system will never provide
 *                  location updates faster than it.<p> @defaults_to 2 milliseconds.
 */
class DisplacementFusedLocationOptions(val smallestDisplacement: Float = EasyLocationConstants.DEFAULT_MIN_DISTANCE,
                                       val fastestInterval: Long = EasyLocationConstants.DEFAULT_FASTEST_INTERVAL,
                                       val priority: Int = EasyLocationConstants.DEFAULT_PRIORITY) : LocationOptions
