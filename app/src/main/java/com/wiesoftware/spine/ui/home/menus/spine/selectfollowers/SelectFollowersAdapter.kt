package com.wiesoftware.spine.ui.home.menus.spine.selectfollowers

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.wiesoftware.spine.R
import com.wiesoftware.spine.data.net.reponses.FollowersData
import com.wiesoftware.spine.databinding.SelectFollowersItemBinding

/**
 * Created by Vivek kumar on 12/21/2020.
 * E-mail:- vivekpcst.kumar@gmail.com
 */
class SelectFollowersAdapter(val followersList:List<FollowersData>,val listener:FollowersEventListener): RecyclerView.Adapter<SelectFollowersAdapter.FollowersHolder>() {
    class FollowersHolder(val selectFollowersItemBinding: SelectFollowersItemBinding):
        RecyclerView.ViewHolder(selectFollowersItemBinding.root) {

    }
    interface FollowersEventListener{
        fun followerSelected(followersData: FollowersData)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        FollowersHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.select_followers_item,
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: FollowersHolder, position: Int) {
        holder.selectFollowersItemBinding.model=followersList[position]
        holder.selectFollowersItemBinding.cbFollowers.setOnCheckedChangeListener { compoundButton, b ->
            if (b){
                listener.followerSelected(followersList[position])
            }
        }
    }

    override fun getItemCount()= followersList.size


}