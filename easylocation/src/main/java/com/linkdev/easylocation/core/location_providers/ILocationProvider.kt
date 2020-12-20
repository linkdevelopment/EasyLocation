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

/**
 * A contract for different location providers
 */
internal interface ILocationProvider {

    /**
     * Request the location updates for this subscriber [locationResultListener]
     *
     * @param locationResultListener
     */
    fun requestLocationUpdates(locationResultListener: LocationResultListener)

    /**
     * stop and cancel the location updates for this provider
     */
    fun stopLocationUpdates()

    /**
     * fetch the latest known location using this provider.
     */
    fun fetchLatestKnownLocation(locationResultListener: LocationResultListener)
}
