package com.example.locationsample_kotlin_android.sample.utils;

import android.content.Context;
import android.content.DialogInterface;
import androidx.appcompat.app.AlertDialog;


/**
 * Created by Mohammed Fareed on 30/5/19.
 */
public class UIUtils {
    public static AlertDialog showBasicDialog(final Context context, String title, String message,
                                              String positiveButton, String negativeButton,
                                              DialogInterface.OnClickListener positiveClickListener,
                                              DialogInterface.OnClickListener negativeClickListener) {
        return new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(positiveButton, positiveClickListener)
                .setNegativeButton(negativeButton, negativeClickListener)
                .show();
    }
}
