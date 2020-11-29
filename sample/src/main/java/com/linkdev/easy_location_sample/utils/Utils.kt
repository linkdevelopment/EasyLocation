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
package com.linkdev.easy_location_sample.utils

import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.location.Location
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import androidx.appcompat.app.AlertDialog
import java.text.SimpleDateFormat
import java.util.*
import kotlin.reflect.KFunction2

object Utils {

    fun showBasicDialog(context: Context?, title: String?, message: String?,
                        positiveButton: String?, negativeButton: String?,
                        onDialogInteraction: KFunction2<@ParameterName(name = "dialogInterface") DialogInterface, @ParameterName(name = "which") Int, Unit>): AlertDialog {
        return AlertDialog.Builder(context!!)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(positiveButton, onDialogInteraction)
                .setNegativeButton(negativeButton, onDialogInteraction)
                .show()
    }

    fun getLocationString(location: Location): String {
        val latLng = String.format(
            Locale.ENGLISH, "%f - %f",
            location.latitude, location.longitude
        )
        val spannableString = SpannableString(latLng)
        spannableString.setSpan(
            ForegroundColorSpan(Color.BLUE), 0, latLng.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        return "\n\n$spannableString    ${getCurrentTime()}"
    }

    private fun getCurrentTime(): String {
        val date = Date()
        return SimpleDateFormat("hh:mm:ss").format(date)
    }
}