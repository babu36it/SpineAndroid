package com.wiesoftware.spine.data.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bumptech.glide.Glide
import com.wiesoftware.spine.R
import com.wiesoftware.spine.data.net.reponses.PostData
import com.wiesoftware.spine.databinding.OwnPostItemBinding


/**
 * Created by Vivek kumar on 1/18/2021.
 * E-mail:- vivekpcst.kumar@gmail.com
 */
class OwnPostAdapter(val postList: List<PostData>, val listener: OwnPostSelectedListener) :
    RecyclerView.Adapter<OwnPostAdapter.OwnPostHolder>() {


    class OwnPostHolder(val ownPostItemBinding: OwnPostItemBinding, val context: Context) :
        RecyclerView.ViewHolder(ownPostItemBinding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        OwnPostHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.own_post_item,
                parent,
                false
            ), parent.context
        )

    override fun onBindViewHolder(holder: OwnPostHolder, position: Int) {
        if (postList[position].type.equals("2")) {
            if (postList[position].files.endsWith(".mp4")) {
                holder.ownPostItemBinding.rrPostVideo.visibility = View.VISIBLE
                holder.ownPostItemBinding.ivvideo.visibility=View.VISIBLE
                holder.ownPostItemBinding.ivPostImageVideo.visibility = View.GONE
                holder.ownPostItemBinding.llHashtags.visibility = View.GONE
                Glide.with(holder.context)
                    .load("https://thespiritualnetwork.com/assets/upload/spine-post/" + postList[position].files)
                    .into(holder.ownPostItemBinding.ivPostImageVideo)
            } else {
                holder.ownPostItemBinding.rrPostVideo.visibility = View.GONE
                holder.ownPostItemBinding.ivPostImageVideo.visibility = View.VISIBLE
                holder.ownPostItemBinding.ivvideo.visibility=View.GONE
                holder.ownPostItemBinding.llHashtags.visibility = View.GONE
                Glide.with(holder.context).load("https://thespiritualnetwork.com/assets/upload/spine-post/" +postList[position].files)
                    .into(holder.ownPostItemBinding.ivPostImageVideo)
            }
        } else {
            holder.ownPostItemBinding.rrPostVideo.visibility = View.GONE
            holder.ownPostItemBinding.ivPostImageVideo.visibility = View.GONE
            holder.ownPostItemBinding.llHashtags.visibility = View.GONE
            holder.ownPostItemBinding.tvHashTags.text = postList[position].hashtag_ids
        }


    }

    override fun getItemCount() = postList.size

    interface OwnPostSelectedListener {
        fun onPostSelected(postData: PostData)
    }

    override fun onViewAttachedToWindow(holder: OwnPostHolder) {
        super.onViewAttachedToWindow(holder)
        val lp: ViewGroup.LayoutParams = holder.itemView.getLayoutParams()
        if (lp != null && lp is StaggeredGridLayoutManager.LayoutParams
            && (holder.getLayoutPosition() === 3 || holder.getLayoutPosition() === 8)
        ) {
            lp.isFullSpan = true
        }
        holder.setIsRecyclable(false)
    }


}