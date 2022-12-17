package com.wiesoftware.spine.data.adapter

import android.content.Context
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.wiesoftware.spine.R
import com.wiesoftware.spine.data.net.reponses.PodDatas
import com.wiesoftware.spine.data.net.reponses.PodcastData
import com.wiesoftware.spine.data.net.reponses.RssItem
import com.wiesoftware.spine.data.net.reponses.episode.EpisodeData
import com.wiesoftware.spine.databinding.ListenPodItemBinding
import kotlinx.android.synthetic.main.listen_pod_item.view.*

/**
 * Created by Vivek kumar on 2/17/2021.
 * Email: vivekpcst.kumar@gmail.com.
 */

class ListenPodcastAdapter(
    val dataList: List<EpisodeData>,
    val listener: PodCastEventsListener,
    val context: Context
) : RecyclerView.Adapter<ListenPodcastAdapter.ListenPodHolder>() {

    class ListenPodHolder(val listenPodItemBinding: ListenPodItemBinding, val context: Context) :
        RecyclerView.ViewHolder(listenPodItemBinding.root) {
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ListenPodHolder(
        DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.listen_pod_item,
            parent,
            false
        ), parent.context
    )

    override fun onBindViewHolder(holder: ListenPodHolder, position: Int) {
        holder.listenPodItemBinding.model = dataList[position]

        for (i in dataList.indices) {
            for (j in 0 until dataList[i].podcast_episodes.size) {
                Glide
                    .with(context)
                    .load(dataList[i].podcast_episodes[j].thumbnail)
                    .centerCrop()
                    .placeholder(R.drawable.ic_photo)
                    .into(holder.itemView.ivThumbnail)
                holder.itemView.tvLanguage.text = dataList[i].podcast_episodes[j].language
                holder.itemView.tvTime.text = dataList[i].podcast_episodes[j].duration
                holder.itemView.tvTitle.text = dataList[i].podcast_episodes[j].title
                holder.itemView.tvPlay.text = dataList[i].podcast_episodes[j].play_count
                holder.itemView.tvFavourite.text = dataList[i].podcast_episodes[j].like
                holder.itemView.tvUserName.text = dataList[i].podcast_episodes[j].user_name
                Glide
                    .with(context)
                    .load(dataList[i].podcast_episodes[j].user_image)
                    .centerCrop()
                    .placeholder(R.drawable.ic_photo)
                    .into(holder.itemView.cvUserProfile)
            }
        }

        //Log.e("dtataa",dataList.toString())
        /* if (dataList[position].podcast_episodes.get(position).duration != null) {
             *//*     var time: Int =
                     (dataList[position].podcast_episodes.get(position).duration).toInt() / 60*//*
//            Log.e("dtataatimee", time.toString())
//            holder.itemView.textView214.text = time.toString() + " min"
        }*/

        /*val bookmark=podcast.bookmarks
        val userLike=podcast.user_like
        if (userLike.equals("1")){
            holder.setTextViewDrawableColor(holder.listenPodItemBinding.textView217)
        }
        if (bookmark.equals("0")){
            holder.listenPodItemBinding.imageButton46.setImageResource(R.drawable.ic_bookmark)
        }else{
            holder.listenPodItemBinding.imageButton46.setImageResource(R.drawable.ic_saved)
        }*/

        /*Glide
            .with(context)
            .load(dataList[position].thumbnail)
            .centerCrop()
           // .placeholder(R.drawable.loading_spinner)
            .into(holder.itemView.imageView25);*/

        /*   holder.listenPodItemBinding.textView217.setOnClickListener {
               listener.onPodcastLike(dataList[position])
           }
           holder.listenPodItemBinding.imageButton46.setOnClickListener {
               listener.onPodcastBookmark(dataList[position])
           }
           holder.listenPodItemBinding.imageView26.setOnClickListener {
               listener.onPodcastUser(dataList[position])
           }
           holder.listenPodItemBinding.imageButton45.setOnClickListener {
               listener.onPodcastDetails(dataList[position])
           }
           holder.listenPodItemBinding.imageView25.setOnClickListener {
               listener.onPodcastDetails(dataList[position])
           }*/
    }

    override fun getItemCount() = dataList.size

    interface PodCastEventsListener {
        fun onPodcastLike(podcastData: RssItem)
        fun onPodcastBookmark(podcastData: RssItem)
        fun onPodcastUser(podcastData: RssItem)
        fun onPodcastDetails(podcastData: RssItem)
    }
}