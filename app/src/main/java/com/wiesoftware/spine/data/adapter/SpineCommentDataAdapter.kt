package com.wiesoftware.spine.data.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.wiesoftware.spine.R
import com.wiesoftware.spine.data.net.reponses.SpineCommentData
import com.wiesoftware.spine.databinding.PostCommentItemBinding

/**
 * Created by Vivek kumar on 1/6/2021.
 * E-mail:- vivekpcst.kumar@gmail.com
 */
class SpineCommentDataAdapter(val commentDataList: List<SpineCommentData>,val listener: SpineCommentEventListener): RecyclerView.Adapter<SpineCommentDataAdapter.CommentHolder>() {
    class CommentHolder(val postCommentItemBinding: PostCommentItemBinding):RecyclerView.ViewHolder(postCommentItemBinding.root) {

    }
    interface SpineCommentEventListener{
        fun onCommentReply(spineCommentData: SpineCommentData)
        fun onViewProfile(spineCommentData: SpineCommentData)
        fun onDeleteComment(spineCommentData: SpineCommentData)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        CommentHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.post_comment_item,
                parent,
                false
            )
        )
    override fun onBindViewHolder(holder: CommentHolder, position: Int) {
        holder.postCommentItemBinding.model=commentDataList[position]
        holder.postCommentItemBinding.button28.setOnClickListener {
            listener.onCommentReply(commentDataList[position])
        }
        holder.postCommentItemBinding.circleImageView5.setOnClickListener {
            listener.onViewProfile(commentDataList[position])
        }
        holder.postCommentItemBinding.textView52.setOnClickListener(object :
            ForYouContentAdapter.DoubleClickListener() {
            override fun onSingleClick(v: View?) {
            }
            override fun onDoubleClick(v: View?) {
                listener.onDeleteComment(commentDataList[position])
            }
        })

        holder.postCommentItemBinding.textView53.setOnClickListener(object :
            ForYouContentAdapter.DoubleClickListener() {
            override fun onSingleClick(v: View?) {
            }
            override fun onDoubleClick(v: View?) {
                listener.onDeleteComment(commentDataList[position])
            }
        })
    }

    override fun getItemCount() = commentDataList.size



}