package com.wiesoftware.spine.data.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.wiesoftware.spine.R
import com.wiesoftware.spine.data.net.reponses.StoryMothData
import com.wiesoftware.spine.databinding.StorySelectTimeItemBinding

/**
 * Created by Vivek kumar on 3/10/2021.
 * Email: vivekpcst.kumar@gmail.com.
 */
class SelectStoryTimeAdapter(val dataList: List<StoryMothData>, val listener: OnTimeSelectedListener):RecyclerView.Adapter<SelectStoryTimeAdapter.StoryMonthHolder>() {
    class StoryMonthHolder(val storySelectTimeItemBinding: StorySelectTimeItemBinding): RecyclerView.ViewHolder(storySelectTimeItemBinding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        StoryMonthHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.story_select_time_item,
                parent,
                false
            )
        )


    override fun onBindViewHolder(holder: StoryMonthHolder, position: Int) {
        holder.storySelectTimeItemBinding.model=dataList[position]
        holder.storySelectTimeItemBinding.textView264.setOnClickListener {
            listener.onTimeSelected(dataList[position])
        }
    }

    override fun getItemCount() = dataList.size

    interface OnTimeSelectedListener{
        fun onTimeSelected(storyMonthData: StoryMothData)
    }

}