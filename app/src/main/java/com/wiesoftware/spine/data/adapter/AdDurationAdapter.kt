package com.wiesoftware.spine.data.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.wiesoftware.spine.R
import com.wiesoftware.spine.data.net.reponses.AdDurationData
import com.wiesoftware.spine.databinding.AdDurationItemBinding

/**
 * Created by Vivek kumar on 5/3/2021.
 * E-mail:- vivekpcst.kumar@gmail.com
 */
class AdDurationAdapter(val dataList: List<AdDurationData>, val listener: OnAdDurationSelectedListener): RecyclerView.Adapter<AdDurationAdapter.AdDurationHolder>() {
    class AdDurationHolder(val adDurationItemBinding: AdDurationItemBinding): RecyclerView.ViewHolder(adDurationItemBinding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        AdDurationHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.ad_duration_item,
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: AdDurationHolder, position: Int) {
        holder.adDurationItemBinding.model = dataList[position]
        holder.adDurationItemBinding.root.setOnClickListener {
            listener.onAdDurationSelected(dataList[position])
        }
    }

    override fun getItemCount() = dataList.size

    interface OnAdDurationSelectedListener{
        fun onAdDurationSelected(adDurationData: AdDurationData)
    }
}