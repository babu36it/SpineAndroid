package com.wiesoftware.spine.data.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.wiesoftware.spine.R
import com.wiesoftware.spine.data.net.reponses.AllUsersData
import com.wiesoftware.spine.databinding.StoryItemBinding

/**
 * Created by Vivek kumar on 10/1/2020.
 * E-mail:- vivekpcst.kumar@gmail.com
 */
class StoryAdapter(val  allUsersData: List<AllUsersData>,val listener: StoryEventListener) : RecyclerView.Adapter<StoryAdapter.StoryHolder>() {
    class StoryHolder(
        val storyItemBinding: StoryItemBinding,
        val context: Context
    ):RecyclerView.ViewHolder(storyItemBinding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        StoryHolder(
            DataBindingUtil.inflate(LayoutInflater.from(parent.context),
            R.layout.story_item,
                parent,
                false
                ),parent.context
        )

    override fun onBindViewHolder(holder: StoryHolder, position: Int) {
        holder.storyItemBinding.modal=allUsersData[position]
        holder.storyItemBinding.button15.setOnClickListener {
            holder.storyItemBinding.button15.text=holder.context.getString(R.string
                .following)
            listener.onStoryFollowing(allUsersData[position])
        }
        holder.storyItemBinding.circleImageView2.setOnClickListener {
            listener.onViewSomeonesProfile(allUsersData[position])
        }
    }

    override fun getItemCount() = allUsersData.size

    interface StoryEventListener{
        fun onStoryFollowing(allUserData: AllUsersData)
        fun onViewSomeonesProfile(allUserData: AllUsersData)
    }
}