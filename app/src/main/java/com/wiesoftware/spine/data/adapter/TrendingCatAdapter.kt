package com.wiesoftware.spine.data.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.wiesoftware.spine.R
import com.wiesoftware.spine.data.net.reponses.HashtagData
import com.wiesoftware.spine.databinding.TrendingCatItemBinding

/**
 * Created by Vivek kumar on 8/31/2020.
 * E-mail:- vivekpcst.kumar@gmail.com
 */
class TrendingCatAdapter(val hashtagDataList: List<HashtagData>) :RecyclerView.Adapter<TrendingCatAdapter.TrendingCatHolder>() {
    class TrendingCatHolder(
        val trendingCatItemBinding: TrendingCatItemBinding
    ): RecyclerView.ViewHolder(trendingCatItemBinding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        TrendingCatHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.trending_cat_item,
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: TrendingCatHolder, position: Int) {
        holder.trendingCatItemBinding.model=hashtagDataList[position]
    }

    override fun getItemCount() = hashtagDataList.size
}