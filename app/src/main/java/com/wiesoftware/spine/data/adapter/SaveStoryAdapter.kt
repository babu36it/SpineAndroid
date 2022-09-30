package com.wiesoftware.spine.data.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.wiesoftware.spine.R
import com.wiesoftware.spine.data.net.reponses.StoryData
import com.wiesoftware.spine.databinding.StorySavedItemBinding
import com.wiesoftware.spine.ui.home.menus.activities.following.FollowingActivityFragment
import com.wiesoftware.spine.ui.home.menus.spine.foryou.BASE_IMAGE

/**
 * Created by Vivek kumar on 1/21/2021.
 * Email: vivekpcst.kumar@gmail.com.
 */
class SaveStoryAdapter(val datalist: List<StoryData>,val listener: SavedStoryClickListener):RecyclerView.Adapter<SaveStoryAdapter.StoryHolder>() {
    var c_pos=0
    class StoryHolder(val storySavedItemBinding: StorySavedItemBinding):RecyclerView.ViewHolder(storySavedItemBinding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        StoryHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.story_saved_item,
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: StoryHolder, position: Int) {
        if (datalist.size >= 4) {
            holder.storySavedItemBinding.model = datalist[position]
        }else{
            if (c_pos < datalist.size){
                holder.storySavedItemBinding.model = datalist[position]
                c_pos++
            }
        }
        if(datalist.size > position) {
            val url= BASE_IMAGE +datalist[position].media_file

            if (FollowingActivityFragment.isVideo(url)){
                holder.storySavedItemBinding.imageView38.visibility = View.VISIBLE
            }else{
                holder.storySavedItemBinding.imageView38.visibility = View.GONE
            }
        }
        holder.storySavedItemBinding.imageView38.setOnClickListener { v->
            if(datalist.size > position) {
                listener.onSavedStoryClick(datalist[position])
            }
        }
        holder.storySavedItemBinding.storyLayout.setOnClickListener { v->
            if(datalist.size > position) {
                listener.onSavedStoryClick(datalist[position])
            }
        }


    }

    override fun getItemCount() = 4

    interface SavedStoryClickListener{
        fun onSavedStoryClick(storyData: StoryData)
    }
}