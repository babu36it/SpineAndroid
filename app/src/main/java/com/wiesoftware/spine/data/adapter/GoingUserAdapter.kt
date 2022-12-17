package com.wiesoftware.spine.data.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.wiesoftware.spine.R
import com.wiesoftware.spine.data.net.reponses.GoingUser
import com.wiesoftware.spine.databinding.GoingUserItemBinding

/**
 * Created by Vivek kumar on 1/27/2021.
 * Email: vivekpcst.kumar@gmail.com.
 */
class GoingUserAdapter(val dataList: List<GoingUser>, val  listner: GoingUsersEventListener): RecyclerView.Adapter<GoingUserAdapter.GoingUserHolder>() {
    var mCount=0
    class GoingUserHolder(val goingUserItemBinding: GoingUserItemBinding): RecyclerView.ViewHolder(goingUserItemBinding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        GoingUserHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.going_user_item,
                parent,false
            )
        )

    override fun onBindViewHolder(holder: GoingUserHolder, position: Int) {
        if (mCount < dataList.size){
            holder.goingUserItemBinding.model=dataList[position]
            mCount++
            holder.goingUserItemBinding.imageView23.setOnClickListener {
                listner.onGoingUserClick(dataList[position])
            }
        }
    }

    override fun getItemCount() = dataList.size

    interface GoingUsersEventListener{
        fun onGoingUserClick(goingUser: GoingUser)
    }
}