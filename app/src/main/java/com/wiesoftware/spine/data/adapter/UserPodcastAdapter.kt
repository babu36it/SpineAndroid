package com.wiesoftware.spine.data.adapter

import android.content.Context
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.wiesoftware.spine.R
import com.wiesoftware.spine.data.net.reponses.PodDatas
import com.wiesoftware.spine.data.net.reponses.PodcastData

import com.wiesoftware.spine.databinding.NewListPodItemBinding
import com.wiesoftware.spine.ui.home.menus.podcasts.userpodcast.UserPodcastActivity
import com.wiesoftware.spine.ui.home.menus.profile.myprofile.MyProfileActivity
import com.wiesoftware.spine.ui.home.menus.profile.tabs.podcasts.PodcastFragment

/**
 * Created by Vivek kumar on 2/18/2021.
 * Email: vivekpcst.kumar@gmail.com.
 */
class UserPodcastAdapter(val dataList: List<PodDatas>, val listener: OnPodEveListener): RecyclerView.Adapter<UserPodcastAdapter.PodHolder>() {
    class PodHolder(val listenPodItemBinding: NewListPodItemBinding,val context: Context): RecyclerView.ViewHolder(listenPodItemBinding.root) {
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
                R.layout.new_list_pod_item,
                parent,
                false
            ),parent.context
        )

    override fun onBindViewHolder(holder: PodHolder, position: Int) {
        //holder.listenPodItemBinding.model=dataList[position]
//        val podcast: PodcastData=dataList[position]
//        val bookmark=podcast.bookmarks
//        val userLike=podcast.user_like
//        holder.listenPodItemBinding.textView217.visibility=View.INVISIBLE
//        holder.listenPodItemBinding.imageButton46.visibility=View.INVISIBLE
//        holder.listenPodItemBinding.imageView25.setOnClickListener {
//            listener.onPodDetails(dataList[position])
//        }
        holder.listenPodItemBinding.textView215.text = "Universal laws from friend of the indians"
        holder.listenPodItemBinding.imageButton45.setOnClickListener {
            listener.onPodDetails(dataList[position])
        }
        /* if (userLike.equals("1")){
             holder.setTextViewDrawableColor(holder.listenPodItemBinding.textView217)
         }
         if (bookmark.equals("0")){
             holder.listenPodItemBinding.imageButton46.setImageResource(R.drawable.ic_bookmark)
         }else{
             holder.listenPodItemBinding.imageButton46.setImageResource(R.drawable.ic_saved)
         }*/
    }

    override fun getItemCount() = 4

    interface OnPodEveListener{
        fun onPodDetails(podcastData: PodDatas)
    }
}