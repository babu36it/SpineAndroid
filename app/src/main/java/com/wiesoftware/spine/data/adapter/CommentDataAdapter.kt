package com.wiesoftware.spine.data.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.wiesoftware.spine.R
import com.wiesoftware.spine.data.net.reponses.CommentData
import com.wiesoftware.spine.databinding.CommentDataItemBinding
import com.wiesoftware.spine.databinding.WelcomeItemBinding

/**
 * Created by Vivek kumar on 10/14/2020.
 * E-mail:- vivekpcst.kumar@gmail.com
 */
class CommentDataAdapter(
    val dataList: ArrayList<CommentData>, private val commentEventListener: CommentEventListener): RecyclerView.Adapter<CommentDataAdapter.DataHolder>() {
    class DataHolder(val commentDataItemBinding: CommentDataItemBinding):
        RecyclerView.ViewHolder(commentDataItemBinding.root) {

    }
    interface CommentEventListener{
        fun onReplyComment(commentData: CommentData)
        fun onViewProfile(commentData: CommentData)
        fun onDeleteComment(commentData: CommentData)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        DataHolder(
            DataBindingUtil.inflate(LayoutInflater.from(parent.context),
            R.layout.comment_data_item,
                parent,
                false
                )
        )

    override fun onBindViewHolder(holder: DataHolder, position: Int) {
        holder.commentDataItemBinding.model=dataList[position]
        holder.commentDataItemBinding.button28.setOnClickListener {
            commentEventListener.onReplyComment(dataList[position])
        }
        holder.commentDataItemBinding.circleImageView5.setOnClickListener {
            commentEventListener.onViewProfile(dataList[position])
        }

        holder.commentDataItemBinding.textView52.setOnClickListener(object :
            ForYouContentAdapter.DoubleClickListener() {
            override fun onSingleClick(v: View?) {
            }
            override fun onDoubleClick(v: View?) {
                commentEventListener.onDeleteComment(dataList[position])
            }
        })

        holder.commentDataItemBinding.textView53.setOnClickListener(object :
            ForYouContentAdapter.DoubleClickListener() {
            override fun onSingleClick(v: View?) {
            }
            override fun onDoubleClick(v: View?) {
                commentEventListener.onDeleteComment(dataList[position])
            }
        })
    }

    override fun getItemCount() = dataList.size
}