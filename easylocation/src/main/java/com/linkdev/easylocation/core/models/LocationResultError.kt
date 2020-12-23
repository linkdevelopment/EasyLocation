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

sealed class LocationResultError(
    val errorCode: LocationErrorCode,
    val errorMessage: String? = null,
    val exception: Exception? = null
) {

    class TimeoutError(errorMessage: String? = EasyLocationConstants.ErrorMessages.TIMEOUT_ERROR) :
        LocationResultError(LocationErrorCode.TIME_OUT, errorMessage)

    class UnknownError(
        errorMessage: String? = EasyLocationConstants.ErrorMessages.UNKNOWN_ERROR,
        exception: Exception? = null
    ) : LocationResultError(LocationErrorCode.UNKNOWN_ERROR, errorMessage, exception)

    class PermissionDenied : LocationResultError(LocationErrorCode.LOCATION_PERMISSION_DENIED, EasyLocationConstants.ErrorMessages.LOCATION_PERMISSION_ERROR)

    class SettingDisabled : LocationResultError(LocationErrorCode.LOCATION_SETTING_DENIED, EasyLocationConstants.ErrorMessages.LOCATION_SETTING_ERROR)

    class ProviderException(exception: Exception?) :
        LocationResultError(LocationErrorCode.PROVIDER_EXCEPTION, exception = exception)
}
