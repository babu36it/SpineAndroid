package com.wiesoftware.spine.data.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.wiesoftware.spine.R
import com.wiesoftware.spine.data.net.reponses.AllUsersData
import com.wiesoftware.spine.data.net.reponses.FollowersData
import com.wiesoftware.spine.databinding.RecommendedFollowersItemBinding

/**
 * Created by Vivek kumar on 9/1/2020.
 * E-mail:- vivekpcst.kumar@gmail.com
 */
class RecommendedFollowersAdapter(val followersData: List<AllUsersData>, val listener: RecommendedFollowersEventListener) : RecyclerView.Adapter<RecommendedFollowersAdapter.RecommendedFollowersHolder>() {
    class RecommendedFollowersHolder(
        val recommendedFollowersItemBinding: RecommendedFollowersItemBinding
    ): RecyclerView.ViewHolder(recommendedFollowersItemBinding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        RecommendedFollowersHolder(
            DataBindingUtil.inflate(LayoutInflater.from(parent
                .context),
                R.layout.recommended_followers_item,
                parent,
                false
                )
        )

    override fun onBindViewHolder(holder: RecommendedFollowersHolder, position: Int) {
        holder.recommendedFollowersItemBinding.model=followersData[position]
        holder.recommendedFollowersItemBinding.ivRec.setOnClickListener {
            listener.onViewsomeonesProfile(followersData[position])
        }
    }

    override fun getItemCount() = followersData.size

    interface RecommendedFollowersEventListener{
        fun onViewsomeonesProfile(allUsersData: AllUsersData)
    }
}