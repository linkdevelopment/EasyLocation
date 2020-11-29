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

import android.location.Location
import java.lang.Exception

sealed class LocationResult(
    val status: Status,
    val location: Location? = null,
    val exception: Exception? = null
) {

    class Success(location: Location) : LocationResult(Status.SUCCESS, location)

    /**
     * Returned if the permission location permission was not granted.
     *
     * If you want us to handle the location permission check out [EasyLocationBaseFragment].
     */
    class LocationPermissionNotGranted : LocationResult(Status.PERMISSION_NOT_GRANTED)

    /**
     * Returned when there is an unknown error from the provider when retrieving the location.
     *
     * @param exception returning the exception if any
     */
    class UnknownError(exception: Exception? = null) :
        LocationResult(Status.UNKNOWN_ERROR, exception = exception)
}
