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