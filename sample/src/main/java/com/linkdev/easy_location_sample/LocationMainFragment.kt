package com.linkdev.easy_location_sample

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import com.google.android.gms.location.LocationRequest
import com.linkdev.easy_location_sample.model.SampleLocationAttributes
import com.linkdev.easy_location_sample.samples.EasyLocationBaseSampleFragment
import com.linkdev.easy_location_sample.samples.EasyLocationSampleFragment
import com.linkdev.easylocation.core.location_providers.fused.options.DisplacementLocationOptions
import com.linkdev.easylocation.core.location_providers.fused.options.TimeLocationOptions
import com.linkdev.easylocation.core.models.LocationRequestType
import kotlinx.android.synthetic.main.fragment_location_main.*
import kotlinx.android.synthetic.main.layout_location_options.*

// Copyright (c) 2020 Link Development All rights reserved.
class LocationMainFragment : Fragment() {

    companion object {
        const val TAG = "LocationMainFragment"

        fun newInstance(): LocationMainFragment {
            return LocationMainFragment()
        }
    }

    private lateinit var mContext: Context
    private lateinit var mListener: ILocationMainFragmentListener

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_location_main, container, false)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is ILocationMainFragmentListener) {
            mListener = context
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mContext = requireActivity()

        setListeners()
        initViews()
    }

    private fun initViews() {
        initLocationOptionsSpinner()
        initPrioritySpinner()
    }

    private fun setListeners() {
        btnEasyLocation.setOnClickListener { onEasyLocationClicked() }
        btnBaseFragment.setOnClickListener { onBaseFragmentClicked() }
    }

    private fun onBaseFragmentClicked() {
        mListener.onSampleClicked(
            EasyLocationBaseSampleFragment.newInstance(getLocationAttributes()),
            EasyLocationBaseSampleFragment.TAG
        )
    }

    private fun onEasyLocationClicked() {
        mListener.onSampleClicked(
            EasyLocationSampleFragment.newInstance(getLocationAttributes()),
            EasyLocationSampleFragment.TAG
        )
    }

    private fun initLocationOptionsSpinner() {
        val optionsList =
            arrayListOf("Interval", "Displacement")

        val arrayAdapter =
            ArrayAdapter(mContext, android.R.layout.simple_spinner_item, optionsList)
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        spOptions.adapter = arrayAdapter
        spOptions.onItemSelectedListener = onLocationOptionChecked()
    }

    private fun onLocationOptionChecked(): AdapterView.OnItemSelectedListener? {
        return object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?, view: View?, position: Int, id: Long
            ) {
                when (position) {
                    0 -> {
                        tlInterval.visibility = View.VISIBLE
                        tlSmallestDisplacement.visibility = View.INVISIBLE
                    }
                    1 -> {
                        tlInterval.visibility = View.INVISIBLE
                        tlSmallestDisplacement.visibility = View.VISIBLE
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
    }

    private fun initPrioritySpinner() {
        val optionsList =
            arrayListOf("High accuracy", "Balanced power accuracy", "PRIORITY_LOW_POWER")

        val arrayAdapter =
            ArrayAdapter(mContext, android.R.layout.simple_spinner_item, optionsList)
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        spPriority.adapter = arrayAdapter
    }

    private fun getMaxRequestTime(): Long {
        return edtMaxRequestTime.text.toString().toLong()
    }

    private fun getSmallestDisplacement(): Float {
        return edtSmallestDisplacement.text.toString().toFloat()
    }

    private fun getIInterval(): Long {
        return edtInterval.text.toString().toLong()
    }

    private fun getPriority(): Int {
        return when (spPriority.selectedItemPosition) {
            0 -> LocationRequest.PRIORITY_HIGH_ACCURACY
            1 -> LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
            else -> LocationRequest.PRIORITY_LOW_POWER
        }
    }

    private fun getFastestInterval(): Long {
        return edtFastestInterval.text.toString().toLong()
    }

    private fun getRequestType(): LocationRequestType {
        return if (checkBoxSingleRequest.isChecked)
            LocationRequestType.ONE_TIME_REQUEST else LocationRequestType.UPDATES
    }

    private fun getLocationAttributes(): SampleLocationAttributes {
        val priority = getPriority()
        val fastestInterval = getFastestInterval()
        val maxRequestTime = getMaxRequestTime()

        val locationOptions =
            if (spOptions.selectedItemPosition == 0) {
                TimeLocationOptions(getIInterval(), fastestInterval, priority)
            } else {
                DisplacementLocationOptions(getSmallestDisplacement(), fastestInterval, priority)
            }

        return SampleLocationAttributes(
            getRequestType(),
            locationOptions,
            maxRequestTime
        )
    }

    interface ILocationMainFragmentListener {
        fun onSampleClicked(
            fragment: Fragment,
            tag: String
        )
    }
}
