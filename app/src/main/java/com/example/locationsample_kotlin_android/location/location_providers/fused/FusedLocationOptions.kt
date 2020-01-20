package com.example.locationsample_kotlin_android.location.location_providers.fused

import com.example.locationsample_kotlin_android.location.Constants
import com.example.locationsample_kotlin_android.location.location_providers.LocationOptions
import com.google.android.gms.location.LocationRequest

/**
 * LocationSampleKotlin_android Created by Mohammed.Fareed on 1/15/2020.
 * * // Copyright (c) 2020 Link. All rights reserved.**/

/**
 *
 * todo
 * @param minTime
 * @param smallestDisplacement
 */
class FusedLocationOptions(val minTime: Long = Constants.MIN_TIME,
                           val fastestInterval: Long = Constants.FASTEST_INTERVAL,
                           val smallestDisplacement: Float = Constants.MIN_DISTANCE,
                           val priority: Int = LocationRequest.PRIORITY_HIGH_ACCURACY) : LocationOptions
