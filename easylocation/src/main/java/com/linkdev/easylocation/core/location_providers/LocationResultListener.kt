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
package com.linkdev.easylocation.core.location_providers

import android.location.Location
import com.linkdev.easylocation.core.models.LocationResult

/**
 * Used for internal communication in the library between different layers to navigate and validate the location result.
 */
internal interface LocationResultListener {

    /**
     * Called in case the location was retrieved successfully.
     */
    fun onLocationRetrieved(location: Location)

    /**
     * Called when there is an error occurred while requesting the location.
     */
    fun onLocationRetrievalError(locationResult: LocationResult?)
}
