package com.wiesoftware.spine.data.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.wiesoftware.spine.R
import com.wiesoftware.spine.data.net.reponses.PostData
import com.wiesoftware.spine.databinding.OwnPostItemBinding

/**
 * Created by Vivek kumar on 1/18/2021.
 * E-mail:- vivekpcst.kumar@gmail.com
 */
class OwnPostAdapter(val postList: List<PostData>,val listener: OwnPostSelectedListener):RecyclerView.Adapter<OwnPostAdapter.OwnPostHolder>() {
    class OwnPostHolder(val ownPostItemBinding: OwnPostItemBinding): RecyclerView.ViewHolder(ownPostItemBinding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        OwnPostHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.own_post_item,
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: OwnPostHolder, position: Int) {
        holder.ownPostItemBinding.model=postList[position]
        val postType=postList[position].type
        if (postType.equals("1")){
            holder.ownPostItemBinding.imgContent.visibility=View.VISIBLE
            holder.ownPostItemBinding.textContent.visibility=View.GONE
        }else{
            holder.ownPostItemBinding.imgContent.visibility=View.GONE
            holder.ownPostItemBinding.textContent.visibility=View.VISIBLE
        }
        holder.ownPostItemBinding.imgContent.setOnClickListener {
            listener.onPostSelected(postList[position])
        }
        holder.ownPostItemBinding.textView134.setOnClickListener {
            listener.onPostSelected(postList[position])
        }
        holder.ownPostItemBinding.postContent.setOnClickListener {
            listener.onPostSelected(postList[position])
        }
    }

    override fun getItemCount() = postList.size

    interface OwnPostSelectedListener{
        fun onPostSelected(postData: PostData)
    }

}