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
package com.linkdev.easylocation.core.location_providers.fused.options

import com.google.android.gms.location.LocationRequest
import com.linkdev.easylocation.core.models.EasyLocationConstants
import kotlinx.android.parcel.Parcelize

/**
 *  Options for using [FusedLocationProvider]
 *
 * @param interval Desired interval for every location update in milliSeconds<p> @defaults_to 3 Seconds.
 * @param priority Get the quality of the request @defaults_to LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY.
 * @param fastestInterval Get the fastest interval of this request, in milliseconds, The system will never provide
 *                  location updates faster than it.<p> @defaults_to 2 milliseconds.
 */
@Parcelize
class TimeLocationOptions(
    val interval: Long = EasyLocationConstants.DEFAULT_INTERVAL,
    val fastestInterval: Long = EasyLocationConstants.DEFAULT_FASTEST_INTERVAL,
    val priority: Int = LocationRequest.PRIORITY_HIGH_ACCURACY,
) : LocationOptions()
