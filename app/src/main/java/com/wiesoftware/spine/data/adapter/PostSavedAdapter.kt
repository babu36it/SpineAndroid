package com.wiesoftware.spine.data.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.wiesoftware.spine.R
import com.wiesoftware.spine.data.net.reponses.PostData
import com.wiesoftware.spine.databinding.SavedPostItemBinding

/**
 * Created by Vivek kumar on 1/21/2021.
 * Email: vivekpcst.kumar@gmail.com.
 */
class PostSavedAdapter(val dataList:List<PostData>,val listener: OnPostClickLisstener): RecyclerView.Adapter<PostSavedAdapter.PostHolder>() {
    var c_pos=0
    class PostHolder(val savedPostItemBinding: SavedPostItemBinding): RecyclerView.ViewHolder(savedPostItemBinding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        PostHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.saved_post_item,
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: PostHolder, position: Int) {
        if (dataList.size >= 4) {
            holder.savedPostItemBinding.model = dataList[position]
        }else{
            if (c_pos < dataList.size){
                holder.savedPostItemBinding.model = dataList[position]
                c_pos++
            }
        }
        holder.savedPostItemBinding.savedL.setOnClickListener { v->
            if(dataList.size > position) {
                listener.onPostClick(dataList[position])
            }
        }

    }

    override fun getItemCount() = 4

    interface OnPostClickLisstener{
        fun onPostClick(postData: PostData)
    }
}