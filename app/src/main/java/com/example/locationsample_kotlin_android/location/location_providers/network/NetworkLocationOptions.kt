package com.example.locationsample_kotlin_android.location.location_providers.network

import com.example.locationsample_kotlin_android.location.Constants
import com.example.locationsample_kotlin_android.location.location_providers.LocationOptions

/**
 * LocationSampleKotlin_android Created by Mohammed.Fareed on 1/15/2020.
 * * // Copyright (c) 2020 Link. All rights reserved.**/
/**
 *  Options for using [NetworkLocationProvider]
 *
 * @param maxWaitTime max time interval for every location update in milliSeconds<p> @defaults_to 10 Seconds
 * @param minDistance Get the minimum distance between location updates in meters<p> @defaults_to 10 meters
 */
class NetworkLocationOptions(val maxWaitTime: Long = Constants.DEFAULT_MAX_WAIT_TIME,
                             val minDistance: Float = Constants.DEFAULT_MIN_DISTANCE) : LocationOptions
