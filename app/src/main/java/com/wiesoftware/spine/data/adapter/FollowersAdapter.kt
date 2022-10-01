package com.wiesoftware.spine.data.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.wiesoftware.spine.R
import com.wiesoftware.spine.data.net.reponses.FollowersData
import com.wiesoftware.spine.databinding.FollowersItemBinding
import com.wiesoftware.spine.ui.home.menus.spine.foryou.BASE_IMAGE

/**
 * Created by Vivek kumar on 1/20/2021.
 * Email: vivekpcst.kumar@gmail.com.
 */
class FollowersAdapter(val dataList: List<FollowersData>,val listener: FollowersFollowListener): RecyclerView.Adapter<FollowersAdapter.FollowersHolder>() {
    class FollowersHolder(val followersItemBinding: FollowersItemBinding,val context: Context): RecyclerView.ViewHolder(followersItemBinding.root) {

    }
    interface FollowersFollowListener{
        fun onFollow(followersData: FollowersData,followStatus: String)
        fun onViewOthersProfile(followersData: FollowersData)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        FollowersHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.followers_item,
                parent,
                false
            ),parent.context
        )

    override fun onBindViewHolder(holder: FollowersHolder, position: Int) {
        holder.followersItemBinding.model=dataList[position]
        val isFollow=dataList[position].is_follow
        if (isFollow.equals("0")){
            holder.followersItemBinding.button60.background=ContextCompat.getDrawable(holder.context,R.drawable.round_button_bg)
            holder.followersItemBinding.button60.text=holder.context.resources.getString(R.string.follows)
            holder.followersItemBinding.button60.setTextColor(ContextCompat.getColor(holder.context,R.color.text_white))
        }else{
            holder.followersItemBinding.button60.background=ContextCompat.getDrawable(holder.context,R.drawable.boarder_round_btn_bg)
            holder.followersItemBinding.button60.text=holder.context.resources.getString(R.string.unfollow)
            holder.followersItemBinding.button60.setTextColor(ContextCompat.getColor(holder.context,R.color.colorPrimaryDark))
        }
        holder.followersItemBinding.button60.setOnClickListener {
            listener.onFollow(dataList[position],isFollow)
            if (isFollow.equals("0")){
                holder.followersItemBinding.button60.background=ContextCompat.getDrawable(holder.context,R.drawable.boarder_round_btn_bg)
                holder.followersItemBinding.button60.text=holder.context.resources.getString(R.string.unfollow)
                holder.followersItemBinding.button60.setTextColor(ContextCompat.getColor(holder.context,R.color.colorPrimaryDark))
            }else{
                holder.followersItemBinding.button60.background=ContextCompat.getDrawable(holder.context,R.drawable.round_button_bg)
                holder.followersItemBinding.button60.text=holder.context.resources.getString(R.string.follows)
                holder.followersItemBinding.button60.setTextColor(ContextCompat.getColor(holder.context,R.color.text_white))
            }
        }
        if (!dataList[position].profile_pic.isNullOrEmpty()) {
            val url = dataList[position].profile_pic
            Glide.with(holder.context)
                .load( url)
                .into( holder.followersItemBinding.imageView20)
        }
        holder.followersItemBinding.imageView20.setOnClickListener {
            listener.onViewOthersProfile(dataList[position])
        }
    }

    override fun getItemCount() = dataList.size
}