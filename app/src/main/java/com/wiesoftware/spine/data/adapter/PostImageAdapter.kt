package com.wiesoftware.spine.data.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.wiesoftware.spine.R
import com.wiesoftware.spine.databinding.PostImageItemBinding
import com.wiesoftware.spine.ui.home.menus.activities.following.FollowingActivityFragment


/**
 * Created by Vivek kumar on 3/31/2021.
 * Email: vivekpcst.kumar@gmail.com.
 */
class PostImageAdapter(val imageList: ArrayList<PostImageItem>): RecyclerView.Adapter<PostImageAdapter.PostImageHolder>() {
    class PostImageHolder(val postImageItemBinding: PostImageItemBinding,val context: Context):RecyclerView.ViewHolder(
        postImageItemBinding.root
    ) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = PostImageHolder(
        DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.post_image_item,
            parent,
            false
        ),parent.context
    )

    override fun onBindViewHolder(holder: PostImageHolder, position: Int) {
        holder.postImageItemBinding.viewmodel=imageList[position]

        val layout = holder.postImageItemBinding.storyLayout

        val lp: StaggeredGridLayoutManager.LayoutParams = layout.layoutParams as StaggeredGridLayoutManager.LayoutParams
        if (imageList.size > 2){
            lp.height = holder.context.resources.getDimensionPixelSize(R.dimen._150dp)
        }else{
            lp.height = ViewGroup.LayoutParams.MATCH_PARENT
        }

        if(imageList.size > 4){
            val extraImg = imageList.size - 4
            holder.postImageItemBinding.tvPost39.text="+$extraImg more"
        }
        if (position == 3){
            holder.postImageItemBinding.tvPost39.visibility=View.VISIBLE
        }else{
            holder.postImageItemBinding.tvPost39.visibility=View.INVISIBLE
        }


        if (FollowingActivityFragment.isVideo(imageList[position].image)){
            holder.postImageItemBinding.imageView38.visibility = View.VISIBLE
        }else{
            holder.postImageItemBinding.imageView38.visibility = View.GONE
        }
    }

    override fun getItemCount() = imageList.size
}