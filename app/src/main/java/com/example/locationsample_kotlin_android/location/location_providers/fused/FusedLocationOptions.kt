package com.example.locationsample_kotlin_android.location.location_providers.fused

import com.example.locationsample_kotlin_android.location.Constants
import com.example.locationsample_kotlin_android.location.location_providers.LocationOptions

/**
 * LocationSampleKotlin_android Created by Mohammed.Fareed on 1/15/2020.
 * * // Copyright (c) 2020 Link. All rights reserved.**/

/**
 *  Options for using [FusedLocationProvider]
 *
 * @param maxWaitTime max time interval for every location update in milliSeconds<p> @defaults_to 10 Seconds
 * @param smallestDisplacement Get the minimum distance between location updates in meters<p> @defaults_to 10 meters
 * @param priority Get the quality of the request @defaults_to LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
 * @param fastestInterval Get the fastest interval of this request, in milliseconds, The system will never provide
 *                  location updates faster than it.<p> @defaults_to 10 meters.
 */
class FusedLocationOptions(val maxWaitTime: Long = Constants.DEFAULT_MAX_WAIT_TIME,
                           val smallestDisplacement: Float = Constants.DEFAULT_MIN_DISTANCE,
                           val priority: Int = Constants.DEFAULT_PRIORITY,
                           val fastestInterval: Long = Constants.DEFAULT_FASTEST_INTERVAL) : LocationOptions
