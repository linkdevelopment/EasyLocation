/**
 * Copyright (c) 2020-present Link Development
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
