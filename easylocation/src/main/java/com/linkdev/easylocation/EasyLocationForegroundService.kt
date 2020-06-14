package com.linkdev.easylocation

import android.app.*
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.location.Location
import android.os.*
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.*
import com.linkdev.easylocation.location_providers.LocationProvidersFactory
import com.linkdev.easylocation.location_providers.LocationResultListener

/**
 * LocationSampleKotlin_android Created by Mohammed.Fareed on 3/5/2020.
 * * // Copyright (c) 2020 LinkDev. All rights reserved.**/
class EasyLocationForegroundService : Service() {


    override fun onBind(intent: Intent?): IBinder? {

    }
}
