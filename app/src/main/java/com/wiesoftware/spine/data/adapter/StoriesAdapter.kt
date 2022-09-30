package com.wiesoftware.spine.data.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.wiesoftware.spine.R
import com.wiesoftware.spine.data.net.reponses.FollwingData
import com.wiesoftware.spine.databinding.StoriesItemBinding

/**
 * Created by Vivek kumar on 8/31/2020.
 * E-mail:- vivekpcst.kumar@gmail.com
 */
class StoriesAdapter(
    val storyList: List<FollwingData>,
    val storyEventListener: StoryEventListener,
    val type: Int,
    val userId: String
) : RecyclerView.Adapter<StoriesAdapter.StoryHolder>() {
    class StoryHolder(
        val storiesItemBinding: StoriesItemBinding,
        val context: Context
    ):RecyclerView.ViewHolder(storiesItemBinding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        StoryHolder(
          DataBindingUtil.inflate(LayoutInflater.from(parent.context),
              R.layout.stories_item,
              parent,
              false
              ),parent.context
        )

    override fun onBindViewHolder(holder: StoryHolder, position: Int) {
        //if(!storyList[position].user_id.equals(userId)) {
            //holder.storiesItemBinding.root.visibility = View.VISIBLE
            holder.storiesItemBinding.model = storyList[position]
            val img = storyList[position].profilePic//stories_data[0].media_file
            Glide.with(holder.storiesItemBinding.circleImageView2)
                .load(/*STORY_IMAGE +*/img)
                .placeholder(R.drawable.ic_profile)
                .into(holder.storiesItemBinding.circleImageView2)
            holder.storiesItemBinding.circleImageView2.setOnClickListener {
                storyEventListener.onStoryClick()
            }
            if (type == 1) {
                holder.storiesItemBinding.textView26.setTextColor(
                    ContextCompat.getColor(
                        holder.context,
                        R.color.text_black
                    )
                )
                holder.storiesItemBinding.textView27.setTextColor(
                    ContextCompat.getColor(
                        holder.context,
                        R.color.text_black
                    )
                )
            } else {
                holder.storiesItemBinding.textView26.setTextColor(
                    ContextCompat.getColor(
                        holder.context,
                        R.color.text_white
                    )
                )
                holder.storiesItemBinding.textView27.setTextColor(
                    ContextCompat.getColor(
                        holder.context,
                        R.color.text_white
                    )
                )
            }
//        }else{
//            holder.storiesItemBinding.root.visibility = View.GONE
//        }
    }

    override fun getItemCount() = storyList.size

    interface StoryEventListener{
        fun onStoryClick()
    }
}