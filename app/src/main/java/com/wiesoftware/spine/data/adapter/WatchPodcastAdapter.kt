package com.wiesoftware.spine.data.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.wiesoftware.spine.R
import com.wiesoftware.spine.data.net.reponses.PodDatas
import com.wiesoftware.spine.databinding.WatchPodItemBinding
import kotlinx.android.synthetic.main.listen_pod_item.view.*

/**
 * Created by Vivek kumar on 5/10/2021.
 * E-mail:- vivekpcst.kumar@gmail.com
 */
class WatchPodcastAdapter(val dataList: List<PodDatas>):RecyclerView.Adapter<WatchPodcastAdapter.WatchPodHolder>() {
    class WatchPodHolder(val watchPodItemBinding: WatchPodItemBinding): RecyclerView.ViewHolder(watchPodItemBinding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = WatchPodHolder(
       DataBindingUtil.inflate(
           LayoutInflater.from(parent.context),
           R.layout.watch_pod_item,
           parent,
           false
       )
    )

    override fun onBindViewHolder(holder: WatchPodHolder, position: Int) {
        holder.watchPodItemBinding.model = dataList[position]

    }

    override fun getItemCount() = dataList.size
}