package com.wiesoftware.spine.ui.home.menus.spine.comment.storycomment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.wiesoftware.spine.R
import com.wiesoftware.spine.data.adapter.ForYouContentAdapter
import com.wiesoftware.spine.data.net.reponses.StoriesCommentData
import com.wiesoftware.spine.databinding.CommentDataItemBinding
import com.wiesoftware.spine.databinding.StoriesCommentItemBinding

/**
 * Created by Vivek kumar on 3/5/2021.
 * Email: vivekpcst.kumar@gmail.com.
 */
class StoryCommentAdapter(val dataList: List<StoriesCommentData>, val listener:StoryCommentListener): RecyclerView.Adapter<StoryCommentAdapter.CommentHolder>() {
    class CommentHolder(val storiesCommentItemBinding: StoriesCommentItemBinding): RecyclerView.ViewHolder(storiesCommentItemBinding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        CommentHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.stories_comment_item,
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: CommentHolder, position: Int) {
        holder.storiesCommentItemBinding.model=dataList[position]
        Glide.with( holder.storiesCommentItemBinding.circleImageView5).load(dataList[position].profilePic).placeholder(R.drawable.ic_profile).into( holder.storiesCommentItemBinding.circleImageView5)
        holder.storiesCommentItemBinding.button28.setOnClickListener {
            listener.onCommentReply(dataList[position])
        }

        holder.storiesCommentItemBinding.textView52.setOnClickListener(object :
            ForYouContentAdapter.DoubleClickListener() {
            override fun onSingleClick(v: View?) {
            }
            override fun onDoubleClick(v: View?) {
                listener.onDeleteComment(dataList[position])
            }
        })

        holder.storiesCommentItemBinding.textView53.setOnClickListener(object :
            ForYouContentAdapter.DoubleClickListener() {
            override fun onSingleClick(v: View?) {
            }
            override fun onDoubleClick(v: View?) {
                listener.onDeleteComment(dataList[position])
            }
        })


    }

    override fun getItemCount() = dataList.size

    interface StoryCommentListener{
        fun onCommentReply(storyCommentData: StoriesCommentData)
        fun onDeleteComment(storyCommentData: StoriesCommentData)
    }
}