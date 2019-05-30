package com.example.locationsample_kotlin_android.utils;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.PopupWindow;
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
