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

internal object EasyLocationConstants {
    const val DEFAULT_MIN_DISTANCE: Float = 5F
    const val DEFAULT_INTERVAL: Long = 5000
    const val DEFAULT_FASTEST_INTERVAL: Long = 1000
    const val DEFAULT_MAX_LOCATION_REQUEST_TIME: Long = 50000
    const val INFINITE_REQUEST_TIME: Long = -1
    val DEFAULT_LOCATION_REQUEST_TYPE: LocationRequestType = LocationRequestType.UPDATES
}
