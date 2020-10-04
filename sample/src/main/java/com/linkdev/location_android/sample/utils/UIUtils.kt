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
package com.linkdev.location_android.sample.utils

import android.content.Context
import android.content.DialogInterface
import androidx.appcompat.app.AlertDialog
import kotlin.reflect.KFunction2

/**
 * Created by Mohammed Fareed on 30/5/19.
 */
object UIUtils {

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
}