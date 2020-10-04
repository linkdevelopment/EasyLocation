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
package com.linkdev.easylocation.location_providers.location_manager.options

import android.location.Criteria
import com.linkdev.easylocation.utils.EasyLocationConstants

/**
 * EasyLocation_Android Created by Mohammed.Fareed on 1/15/2020.
 * * // Copyright (c) 2020 LinkDev. All rights reserved.**/
/**
 *  Options for using [LocationManagerProvider]
 *
 * @param minDistance Get the minimum distance between location updates in meters, Default 10 meters.
 * @param locationManagerProvider One of [LocationManagerProviderTypes] , There are multiple sensors in the device
 * to determine device location using the Location manger framework you can specify which specific provider to use.
 * @param criteria Should be applied in case of [LocationManagerProviderTypes.CRITERIA_BASED] only otherwise the value will be ignored.
 */
class DisplacementLocationManagerOptions(val minDistance: Float = EasyLocationConstants.DEFAULT_MIN_DISTANCE,
                                         override val locationManagerProvider: LocationManagerProviderTypes = LocationManagerProviderTypes.GPS,
                                         override val criteria: Criteria? = null) :
        LocationManagerOptions(locationManagerProvider, criteria)
