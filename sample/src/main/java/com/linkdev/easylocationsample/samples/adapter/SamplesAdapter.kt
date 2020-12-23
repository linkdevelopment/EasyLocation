package com.linkdev.easylocationsample.samples.adapter

import android.content.Context
import android.location.Location
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.linkdev.easylocationsample.R
import com.linkdev.easylocationsample.utils.Utils
import java.util.*
import kotlin.collections.ArrayList

class SamplesAdapter(
    private val mContext: Context,
    var mData: ArrayList<Location>
) : RecyclerView.Adapter<SampleViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SampleViewHolder {
        return SampleViewHolder(
            LayoutInflater.from(mContext).inflate(R.layout.item_sample_location, parent, false)
        )
    }

    override fun onBindViewHolder(holder: SampleViewHolder, position: Int) {
        holder.bind(mData[position])
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    fun addItem(location: Location) {
        mData.add(location)
        notifyItemInserted(mData.size)
    }

    fun clear() {
        mData = arrayListOf()
        notifyDataSetChanged()
    }
}
