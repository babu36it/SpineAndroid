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

/**
 * Created by Vivek kumar on 1/21/2021.
 * Email: vivekpcst.kumar@gmail.com.
 */
class FollowingAdapter(var dataList: List<FollowersData>, val listener:FollowingUnfollowListener): RecyclerView.Adapter<FollowingAdapter.FollowingHolder>() {

    class FollowingHolder(val followersItemBinding: FollowersItemBinding, val context: Context): RecyclerView.ViewHolder(followersItemBinding.root)
    interface FollowingUnfollowListener{
        fun onFollow(followersData: FollowersData)
        fun onViewOthersProfile(followersData: FollowersData)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        FollowingAdapter.FollowingHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.followers_item,
                parent,
                false
            ), parent.context
        )

    override fun onBindViewHolder(holder: FollowingAdapter.FollowingHolder, position: Int) {
        holder.followersItemBinding.model=dataList[position]
        holder.followersItemBinding.button60.setOnClickListener {
            listener.onFollow(dataList[position])
            holder.followersItemBinding.button60.background=ContextCompat.getDrawable(holder.context,R.drawable.round_button_bg)
            holder.followersItemBinding.button60.backgroundTintList =
                ContextCompat.getColorStateList(holder.context,R.color.colorPrimaryDark)
            holder.followersItemBinding.button60.text=holder.context.resources.getString(R.string.follows)
            holder.followersItemBinding.button60.setTextColor(ContextCompat.getColor(holder.context,R.color.text_white))

        }
        holder.followersItemBinding.imageView20.setOnClickListener {
            listener.onViewOthersProfile(dataList[position])
        }
        if (dataList[position].profile_pic != "") {
            val url = dataList[position].profile_pic
            Glide.with(holder.context)
                .load( url)
                .into( holder.followersItemBinding.imageView20)
        }else{
            Glide.with(holder.context)
                .load( R.drawable.ic_profile)
                .into( holder.followersItemBinding.imageView20)
        }
    }


    fun filterList(filterlist: ArrayList<FollowersData>) {
        dataList = filterlist
        notifyDataSetChanged()
    }

    override fun getItemCount() = dataList.size

}