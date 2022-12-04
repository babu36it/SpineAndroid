package com.wiesoftware.spine.data.adapter

import android.content.Context
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.wiesoftware.spine.R
import com.wiesoftware.spine.data.net.reponses.PodDatas
import com.wiesoftware.spine.databinding.AdpPodcastItemBinding
import com.wiesoftware.spine.databinding.ListenPodItemBinding
import kotlinx.coroutines.CoroutineScope

class ListPodcastAdapter(val dataList: List<PodDatas>, val listener: OnPodEveListener) :
    RecyclerView.Adapter<ListPodcastAdapter.PodHolder>() {
    class PodHolder(val adpPodcastItemBinding: AdpPodcastItemBinding, val context: Context) :
        RecyclerView.ViewHolder(adpPodcastItemBinding.root) {
        fun setTextViewDrawableColor(textView: TextView) {
            for (drawable in textView.compoundDrawablesRelative) {
                if (drawable != null) {
                    drawable.colorFilter =
                        PorterDuffColorFilter(
                            ContextCompat.getColor(context, R.color.color_red),
                            PorterDuff.Mode.SRC_IN
                        )
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        PodHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.adp_podcast_item,
                parent,
                false
            ), parent.context
        )

    override fun onBindViewHolder(holder: PodHolder, position: Int) {
        if (!dataList[position].rss_data.image.isEmpty()) {
            Glide.with(holder.context).load(dataList[position].rss_data.image)
                .into(holder.adpPodcastItemBinding.ivPodcastImage)

        }

        holder.adpPodcastItemBinding.tvTitle.text = dataList[position].rss_data.title
        holder.adpPodcastItemBinding.tvLanguage.text = dataList[position].rss_data.language
        holder.adpPodcastItemBinding.tvTime.text = dataList[position].created_on

        holder.itemView.setOnClickListener {
            listener.onPodDetails(dataList[position])
        }


    }

    override fun getItemCount() = dataList.size

    interface OnPodEveListener {
        fun onPodDetails(podcastData: PodDatas)
    }
}