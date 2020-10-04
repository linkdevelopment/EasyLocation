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
package com.linkdev.easylocation

import androidx.lifecycle.LiveData
import com.linkdev.easylocation.location_providers.LocationOptions
import com.linkdev.easylocation.location_providers.LocationProvidersTypes
import com.linkdev.easylocation.location_providers.LocationResult
import com.linkdev.easylocation.utils.EasyLocationConstants

// Created by Mohammed Fareed on 9/27/2020.
// Copyright (c) 2020 Link Development All rights reserved.
interface IEasyLocationObserver {

    fun requestLocationUpdates(
        locationProviderType: LocationProvidersTypes,
        locationOptions: LocationOptions
    ): LiveData<LocationResult>

    fun stopLocationUpdates()

    fun setMaxLocationRequestTime(maxLocationRequestTime: Long = EasyLocationConstants.DEFAULT_MAX_LOCATION_REQUEST_TIME)

    fun setSingleLocationRequest(singleLocationRequest: Boolean = false)
}
