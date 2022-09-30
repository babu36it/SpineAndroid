package com.wiesoftware.spine.ui.home.menus.spine.comment.reply

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.wiesoftware.spine.R
import com.wiesoftware.spine.data.net.reponses.CommentReplyData
import com.wiesoftware.spine.databinding.RepliesCommentItemBinding

/**
 * Created by Vivek kumar on 3/1/2021.
 * Email: vivekpcst.kumar@gmail.com.
 */
class ReplyCommentAdapter(val dataList: List<CommentReplyData>, val listener: ReplyEventListener): RecyclerView.Adapter<ReplyCommentAdapter.CommentHolder>() {
    class CommentHolder(val repliesCommentItemBinding: RepliesCommentItemBinding): RecyclerView.ViewHolder(repliesCommentItemBinding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        CommentHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.replies_comment_item,
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: CommentHolder, position: Int) {
        holder.repliesCommentItemBinding.model=dataList[position]
        holder.repliesCommentItemBinding.button89.setOnClickListener {
            listener.onReply(dataList[position])
        }
    }

    override fun getItemCount() = dataList.size

    interface ReplyEventListener{
        fun onReply(commentReplyData: CommentReplyData)
    }
}