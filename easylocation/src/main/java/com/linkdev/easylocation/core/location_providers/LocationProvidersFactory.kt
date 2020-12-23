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

import android.Manifest
import android.content.Context
import androidx.annotation.RequiresPermission
import com.linkdev.easylocation.core.location_providers.fused.FusedLocationProvider
import com.linkdev.easylocation.core.location_providers.fused.options.LocationOptions
import com.linkdev.easylocation.core.models.EasyLocationConstants
import com.linkdev.easylocation.core.models.LocationProvidersTypes

/**
 * Use this factory to manage different providers currently there is only the [FusedLocationProvider]
 *
 * @param mContext Context
 */
internal class LocationProvidersFactory(
    private val mContext: Context,
) {

    /**
     * Returns the [ILocationProvider] based on the [locationProvidersTypes] and the provided [locationOptions].
     *
     * @param locationProvidersTypes Represents the location provider used to retrieve the location one of [LocationProvidersTypes] enum values.
     *
     * @param locationOptions The specs required for retrieving location info, Depending on [locationProvidersTypes]:
     *      + [DisplacementFusedLocationOptions]
     *      + [TimeFusedLocationOptions]
     *
     * @return Location provider based on the [locationProvidersTypes].
     */
    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    fun getLocationProvider(
        locationProvidersTypes: LocationProvidersTypes,
        locationOptions: LocationOptions = EasyLocationConstants.DEFAULT_FUSED_OPTIONS
    ): ILocationProvider {
        return when (locationProvidersTypes) {
            LocationProvidersTypes.FUSED_LOCATION_PROVIDER -> {
                initializeFusedLocationProvider(locationOptions)
            }
        }
    }

    /**
     * Initializes fused location provider.
     */
    private fun initializeFusedLocationProvider(locationOptions: LocationOptions = EasyLocationConstants.DEFAULT_FUSED_OPTIONS): ILocationProvider {
        return FusedLocationProvider(mContext, locationOptions)
    }
}
