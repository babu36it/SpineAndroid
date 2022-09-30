package com.wiesoftware.spine.data.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.wiesoftware.spine.R
import com.wiesoftware.spine.data.net.reponses.RssItem
import com.wiesoftware.spine.databinding.RssEpisodeItemBinding
import kotlinx.android.synthetic.main.rss_episode_item.view.*

/**
 * Created by Vivek kumar on 4/9/2021.
 * Email: vivekpcst.kumar@gmail.com.
 */
class RssItemAdapter(val items: List<RssItem>): RecyclerView.Adapter<RssItemAdapter.RssItemHolder>() {
    class RssItemHolder(val rssEpisodeItemBinding: RssEpisodeItemBinding):RecyclerView.ViewHolder(rssEpisodeItemBinding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = RssItemHolder(
        DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.rss_episode_item,
            parent,
            false
        )
    )

    override fun onBindViewHolder(holder: RssItemHolder, position: Int) {
        holder.rssEpisodeItemBinding.model=items[position]
        holder.itemView.imageButton46.visibility= View.GONE
    }

    override fun getItemCount() = items.size
}