package com.wiesoftware.spine.data.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.wiesoftware.spine.R
import com.wiesoftware.spine.data.net.reponses.EventCommentData
import com.wiesoftware.spine.databinding.EventCommentDataItemBinding

/**
 * Created by Vivek kumar on 1/14/2021.
 * E-mail:- vivekpcst.kumar@gmail.com
 */
class EventReplyAdapter(val list: List<EventCommentData>): RecyclerView.Adapter<EventReplyAdapter.ReplyHolder>() {
    class ReplyHolder(val eventCommentDataItemBinding: EventCommentDataItemBinding): RecyclerView.ViewHolder(eventCommentDataItemBinding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ReplyHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.event_comment_data_item,
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: ReplyHolder, position: Int) {
        holder.eventCommentDataItemBinding.model=list[position]
    }

    override fun getItemCount() = list.size
}