package com.linkdev.easylocationsample

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.linkdev.easylocationsample.samples.SampleEasyLocationBaseFragment
import com.linkdev.easylocationsample.samples.SampleEasyLocationFragment
import kotlinx.android.synthetic.main.fragment_location_main.*

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
    }

    private fun setListeners() {
        btnEasyLocation.setOnClickListener { onEasyLocationClicked() }
        btnBaseFragment.setOnClickListener { onBaseFragmentClicked() }
    }

    private fun onBaseFragmentClicked() {
        mListener.onSampleClicked(
            SampleEasyLocationBaseFragment.newInstance(),
            SampleEasyLocationBaseFragment.TAG
        )
    }

    private fun onEasyLocationClicked() {
        mListener.onSampleClicked(
            SampleEasyLocationFragment.newInstance(),
            SampleEasyLocationFragment.TAG
        )
    }

    interface ILocationMainFragmentListener {
        fun onSampleClicked(
            fragment: Fragment,
            tag: String
        )
    }
}
