package com.wiesoftware.spine.ui.home.menus.spine.rec_followers

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.wiesoftware.spine.R
import com.wiesoftware.spine.data.net.reponses.AllUsersData
import com.wiesoftware.spine.databinding.RecMembersItemBinding

/**
 * Created by Vivek kumar on 12/17/2020.
 * E-mail:- vivekpcst.kumar@gmail.com
 */
class RecommendedFollowersAdapter(val allUsersData: List<AllUsersData>,
val listener: RecommendedFollowersListener
                                  ) :RecyclerView.Adapter<RecommendedFollowersAdapter.MemberHolder>() {
    class MemberHolder(val recMembersItemBinding: RecMembersItemBinding,val context: Context):RecyclerView.ViewHolder(recMembersItemBinding.root) {

    }
    interface RecommendedFollowersListener{
        fun onFollowClick(allUsersData: AllUsersData, position: Int)
        fun onViewProfile(allUsersData: AllUsersData)
        fun onUnfollowUser(allUsersData: AllUsersData,position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        MemberHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.rec_members_item,
                parent,
                false
            ),parent.context
        )

    override fun onBindViewHolder(holder: MemberHolder, position: Int) {
        holder.recMembersItemBinding.model=allUsersData[position]

        if(allUsersData[position].followStatus.equals("1")){
            holder.recMembersItemBinding.followBtn.text=holder.context.getString(R.string.following)
        }else{
            holder.recMembersItemBinding.followBtn.text=holder.context.getString(R.string.follows)
        }



        holder.recMembersItemBinding.followBtn.setOnClickListener {

            if(allUsersData[position].followStatus.equals("1")){
                listener.onUnfollowUser(allUsersData[position],position)
                holder.recMembersItemBinding.followBtn.text=holder.context.getString(R.string.follows)
            }else{
                listener.onFollowClick(allUsersData[position],position)
                holder.recMembersItemBinding.followBtn.text=holder.context.getString(R.string.following)
            }
        }
        holder.recMembersItemBinding.imageView9.setOnClickListener {
            listener.onViewProfile(allUsersData[position])
        }
        holder.recMembersItemBinding.textView84.setOnClickListener {
            listener.onViewProfile(allUsersData[position])
        }
    }

    override fun getItemCount() = allUsersData.size
}