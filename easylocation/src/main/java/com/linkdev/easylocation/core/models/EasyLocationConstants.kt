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
package com.linkdev.easylocation.core.models

import com.linkdev.easylocation.core.location_providers.fused.options.LocationOptions
import com.linkdev.easylocation.core.location_providers.fused.options.TimeLocationOptions

internal object EasyLocationConstants {
    const val DEFAULT_MIN_DISTANCE: Float = 5F
    const val DEFAULT_INTERVAL: Long = 5000
    const val DEFAULT_FASTEST_INTERVAL: Long = 1000
    const val DEFAULT_LOCATION_REQUEST_TIMEOUT: Long = 50000
    val DEFAULT_EASY_LOCATION_PRIORITY: EasyLocationPriority = EasyLocationPriority.PRIORITY_HIGH_ACCURACY
    const val INFINITE_REQUEST_TIME: Long = -1
    val DEFAULT_LOCATION_REQUEST_TYPE: LocationRequestType = LocationRequestType.UPDATES

    val DEFAULT_FUSED_OPTIONS: LocationOptions = TimeLocationOptions()

    object ErrorMessages {
        const val UNKNOWN_PROVIDER =
            "You are using an unknown provider should be [FusedLocationProvider]"
        const val FUSED_OPTIONS_TYPE_ERROR =
            "Fused location options should be one of [DisplacementFusedLocationOptions, TimeFusedLocationOptions]"
        const val TIMEOUT_ERROR =
            "Location request timed out without any response from the provider with these options"
        const val UNKNOWN_ERROR =
            "An unknown error has occurred"
        const val LOCATION_PERMISSION_ERROR =
            "Location permission was not provided, To use this feature enable and try again."
        const val LOCATION_SETTING_ERROR =
            "Location settings was not enabled, To use this feature enable and try again."
    }
}
