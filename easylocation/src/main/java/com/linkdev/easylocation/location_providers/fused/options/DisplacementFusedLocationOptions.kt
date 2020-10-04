package com.linkdev.easylocation.location_providers.fused.options

import com.linkdev.easylocation.utils.EasyLocationConstants

/**
 * EasyLocation_Android Created by Mohammed.Fareed on 1/15/2020.
 * * // Copyright (c) 2020 LinkDev. All rights reserved.**/

/**
 *  Options for using [FusedLocationProvider]
 *
 * @param smallestDisplacement Get the minimum distance between location updates in meters<p> @defaults_to [EasyLocationConstants.DEFAULT_MIN_DISTANCE]
 * @param priority Get the quality of the request @defaults_to [EasyLocationConstants.DEFAULT_PRIORITY]
 * @param fastestInterval Get the fastest interval of this request, in milliseconds, The system will never provide
 *                  location updates faster than it.<p> @defaults_to [EasyLocationConstants.DEFAULT_FASTEST_INTERVAL].
 */
class DisplacementFusedLocationOptions(
    val smallestDisplacement: Float = EasyLocationConstants.DEFAULT_MIN_DISTANCE,
    val fastestInterval: Long = EasyLocationConstants.DEFAULT_FASTEST_INTERVAL,
    val priority: Int = EasyLocationConstants.DEFAULT_PRIORITY
) : FusedLocationOptions
