package com.wiesoftware.spine.ui.home.menus.profile.someonesprofile.followers

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.wiesoftware.spine.R
import com.wiesoftware.spine.data.net.reponses.FollowersData
import com.wiesoftware.spine.databinding.FfItemBinding

/**
 * Created by Vivek kumar on 2/24/2021.
 * Email: vivekpcst.kumar@gmail.com.
 */
class FFAdapter(val dataList: List<FollowersData>,val listener:FFEventListener):RecyclerView.Adapter<FFAdapter.FFHolder>() {
    class FFHolder(val ffItemBinding: FfItemBinding,val context: Context): RecyclerView.ViewHolder(ffItemBinding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        FFHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.ff_item,
                parent,
                false
            ),parent.context
        )

    override fun onBindViewHolder(holder: FFHolder, position: Int) {
        holder.ffItemBinding.model=dataList[position]
        holder.ffItemBinding.followBtn.setOnClickListener {
            holder.ffItemBinding.followBtn.text=holder.context.getString(R.string.following)
            listener.onFollow(dataList[position])
        }
    }

    override fun getItemCount() = dataList.size

    interface FFEventListener{
        fun onFollow(followersData: FollowersData)
    }
}