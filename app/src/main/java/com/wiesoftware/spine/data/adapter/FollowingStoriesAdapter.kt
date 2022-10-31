package com.wiesoftware.spine.data.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.wiesoftware.spine.R
import com.wiesoftware.spine.data.net.reponses.FollwingData
import com.wiesoftware.spine.data.net.reponses.StoryData
import com.wiesoftware.spine.databinding.StoriesFollowingItemBinding
import com.wiesoftware.spine.ui.home.menus.spine.foryou.STORY_IMAGE

/**
 * Created by Vivek kumar on 9/3/2020.
 * E-mail:- vivekpcst.kumar@gmail.com
 */

class FollowingStoriesAdapter(val storyData: List<FollwingData>,val listener: FollowingStoryEventListener) :RecyclerView.Adapter<FollowingStoriesAdapter.StoriesHolder>() {
    class StoriesHolder(
        val storiesFollowingItemBinding: StoriesFollowingItemBinding
    ): RecyclerView.ViewHolder(storiesFollowingItemBinding.root) {

    }

    interface FollowingStoryEventListener{
        fun onClick(storyData: FollwingData)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        StoriesHolder(
            DataBindingUtil.inflate(LayoutInflater.from(parent.context),
                R.layout.stories_following_item,
                parent,
                false
                )
        )

    override fun onBindViewHolder(holder: StoriesHolder, position: Int) {
        holder.storiesFollowingItemBinding.model=storyData[position]
        val img=storyData[position].stories_data[0].media_file
        Glide.with(holder.storiesFollowingItemBinding.circleImageView2)
            .load(STORY_IMAGE+img)
            .placeholder(R.drawable.ef_folder_placeholder)
            .into(holder.storiesFollowingItemBinding.circleImageView2)

        holder.storiesFollowingItemBinding.circleImageView2.setOnClickListener {
            listener.onClick(storyData[position])
        }
    }

    override fun getItemCount() = storyData.size

}