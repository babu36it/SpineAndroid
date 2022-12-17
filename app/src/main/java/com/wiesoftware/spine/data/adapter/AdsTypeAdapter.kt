package com.wiesoftware.spine.data.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.wiesoftware.spine.R
import com.wiesoftware.spine.data.net.reponses.adsmanagment.AdsTypeData
import com.wiesoftware.spine.databinding.AdpTypeItemBinding

class AdsTypeAdapter(val dataList: List<AdsTypeData>, val listener: onAdTypeSelectedListner): RecyclerView.Adapter<AdsTypeAdapter.AdsTypeViewHolder>() {
    class AdsTypeViewHolder(val adsTypeItemBinding: AdpTypeItemBinding): RecyclerView.ViewHolder(adsTypeItemBinding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        AdsTypeViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.adp_type_item,
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: AdsTypeViewHolder, position: Int) {
        holder.adsTypeItemBinding.model = dataList[position]
        holder.adsTypeItemBinding.root.setOnClickListener {
            listener.onAdTypeSelected(dataList[position])
        }
    }

    override fun getItemCount() = dataList.size

    interface onAdTypeSelectedListner{
        fun onAdTypeSelected(adsTypeData: AdsTypeData)
    }
}