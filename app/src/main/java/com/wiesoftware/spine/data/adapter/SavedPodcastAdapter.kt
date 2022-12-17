package com.wiesoftware.spine.data.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.wiesoftware.spine.R
import com.wiesoftware.spine.data.net.reponses.PodData
import com.wiesoftware.spine.data.net.reponses.PodDatas
import com.wiesoftware.spine.data.net.reponses.PodcastData
import com.wiesoftware.spine.databinding.SavedPodItemBinding

/**
 * Created by Vivek kumar on 1/21/2021.
 * Email: vivekpcst.kumar@gmail.com.
 */
class SavedPodcastAdapter(val list: List<PodDatas>): RecyclerView.Adapter<SavedPodcastAdapter.PodHolder>() {
    class PodHolder(val savedPodItemBinding: SavedPodItemBinding):RecyclerView.ViewHolder(savedPodItemBinding.root) {

    }
    var c_pos=0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        PodHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.saved_pod_item,
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: PodHolder, position: Int) {
        if (list.size >= 4) {
            holder.savedPodItemBinding.model = list[position]
        }
        else{
            if (c_pos < list.size){
                holder.savedPodItemBinding.model=list[position]
                c_pos++
            }
        }
    }

    override fun getItemCount() = 4
}