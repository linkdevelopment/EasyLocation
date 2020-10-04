package com.linkdev.location_android.sample

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.linkdev.easylocation.location_providers.LocationError
import com.linkdev.easylocation.location_providers.LocationProvidersTypes
import com.linkdev.easylocation.location_providers.fused.options.DisplacementFusedLocationOptions
import com.linkdev.easylocation.location_providers.location_manager.options.TimeLocationManagerOptions
import com.linkdev.location_android.R
import com.linkdev.location_android.sample.base.LocationBaseLocationFragment
import com.linkdev.location_android.sample.utils.UIUtils
import kotlinx.android.synthetic.main.location_sample_fragment.*
import java.text.SimpleDateFormat
import java.util.*

class SampleLocationFragmentLocation : LocationBaseLocationFragment() {

    private lateinit var mContext: Context

    companion object {
        const val TAG = "SampleLocationFragment"

        fun newInstance(): SampleLocationFragmentLocation {
            return SampleLocationFragmentLocation()
        }
    }

    private var mRetrievingLocation = false
    private var isSingleRequest = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.location_sample_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (activity != null)
            mContext = activity!!

        initViews()
        setListeners()
    }

    private fun initViews() {
    }

    private fun setListeners() {
        btnLocate.setOnClickListener { onLocationClicked() }
        checkBoxSingleRequest.setOnCheckedChangeListener(isSingleRequestChecked())
    }

    private fun isSingleRequestChecked(): CompoundButton.OnCheckedChangeListener {
        return CompoundButton.OnCheckedChangeListener { _, isChecked ->
            isSingleRequest = isChecked
        }
    }

    private fun onLocationClicked() {
        applyButtonStyle(mRetrievingLocation)
        if (mRetrievingLocation) {

            stopLocation()
        } else {
            txtLocation.text = ""

            getLocation(
                LocationProvidersTypes.LOCATION_MANAGER_PROVIDER,
                TimeLocationManagerOptions(),
                isSingleRequest
            )
        }
        mRetrievingLocation = !mRetrievingLocation
    }

    override fun onLocationRetrieved(location: Location) {
        if (isSingleRequest)
            applyButtonStyle(true)

        val latLng = String.format(
            Locale.ENGLISH, "%f - %f",
            location.latitude, location.longitude
        )
        txtLocation.text = "${txtLocation.text}\n\n$latLng    ${getCurrentTime()}"
    }

    private fun getCurrentTime(): String {
        val date = Date()
        return SimpleDateFormat("hh:mm:ss").format(date)
    }

    override fun onLocationRetrievalError(locationError: LocationError) {
        mRetrievingLocation = false
        applyButtonStyle(true)

        when (locationError) {
            LocationError.SHOULD_SHOW_RATIONAL -> {
                val alertDialog = UIUtils.showBasicDialog(
                    mContext, null, getString(R.string.nearby_location_permission_message),
                    getString(R.string.grant_permission), getString(R.string.cancel),
                    this::onLocationPermissionDialogInteraction
                )
                alertDialog.setOnCancelListener { dialogInterface ->
                    onLocationPermissionDialogInteraction(
                        dialogInterface,
                        DialogInterface.BUTTON_NEGATIVE
                    )
                }
            }
            LocationError.LOCATION_SETTING_DENIED ->
                Toast.makeText(mContext, "Location setting denied", Toast.LENGTH_SHORT).show()
            LocationError.LOCATION_PERMISSION_DENIED ->
                Toast.makeText(mContext, "Location permission denied", Toast.LENGTH_SHORT).show()
            LocationError.LOCATION_ERROR ->
                Toast.makeText(
                    mContext,
                    "Something went wrong and the location returned as null",
                    Toast.LENGTH_SHORT
                ).show()
        }
    }

    private fun onLocationPermissionDialogInteraction(
        dialogInterface: DialogInterface,
        which: Int
    ) {
        dialogInterface.dismiss()
        when (which) {
            DialogInterface.BUTTON_POSITIVE -> {
                val intent = Intent()
                intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                val uri =
                    Uri.fromParts("package", mContext.packageName, null)
                intent.data = uri
                startActivity(intent)
            }
            DialogInterface.BUTTON_NEGATIVE -> {
                Toast.makeText(
                    mContext,
                    "You will not be able to use this feature. ",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun applyButtonStyle(locate: Boolean) {
        if (locate) {
            btnLocate.text = getString(R.string.start_location)
            btnLocate.background.colorFilter = PorterDuffColorFilter(
                ContextCompat.getColor(mContext, R.color.colorPrimary), PorterDuff.Mode.MULTIPLY
            )
        } else {
            btnLocate.text = getString(R.string.stop_location)
            btnLocate.background.colorFilter = PorterDuffColorFilter(
                ContextCompat.getColor(mContext, R.color.colorAccent), PorterDuff.Mode.MULTIPLY
            )
        }
    }
}
