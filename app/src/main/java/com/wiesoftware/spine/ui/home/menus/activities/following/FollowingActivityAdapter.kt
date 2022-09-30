package com.wiesoftware.spine.ui.home.menus.activities.following

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.wiesoftware.spine.R
import com.wiesoftware.spine.data.net.reponses.ActivitiesData
import com.wiesoftware.spine.databinding.ActivityItemBinding
import com.wiesoftware.spine.ui.home.menus.activities.following.FollowingActivityFragment.Companion.isVideo
import com.wiesoftware.spine.ui.home.menus.spine.foryou.BASE_IMAGE

/**
 * Created by Vivek kumar on 1/4/2021.
 * E-mail:- vivekpcst.kumar@gmail.com
 */
class FollowingActivityAdapter(val dataList: List<ActivitiesData>,val listener: FollowingActivityClickListener):RecyclerView.Adapter<FollowingActivityAdapter.VHolder>() {
    class VHolder(val activityItemBinding: ActivityItemBinding): RecyclerView.ViewHolder(activityItemBinding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = VHolder(
        DataBindingUtil.inflate(LayoutInflater.from(parent.context),
        R.layout.activity_item,
            parent,
            false
            )
    )

    override fun onBindViewHolder(holder: VHolder, position: Int) {
            holder.activityItemBinding.model=dataList[position]
           holder.activityItemBinding.textView65.text = dataList[position].displayName ?: dataList[position].userNameFromId
        holder.activityItemBinding.textView66.text=dataList[position].createdOnTimeAgo
        if (dataList[position].tbl.equals("POST")){
            val action=dataList[position].tblAction
            val desc=if(action.contains("like",true)){
                "${dataList[position].displayName ?: dataList[position].userNameFromId} has liked ${dataList[position].oppDisplayName ?: dataList[position].uName}'s post."
            }else if (action.contains("comment")){
                "${dataList[position].displayName ?: dataList[position].userNameFromId} has commented on ${dataList[position].oppDisplayName ?: dataList[position].uName}'s post."
            }else if (action.contains("save")){
                "${dataList[position].displayName ?: dataList[position].userNameFromId} has saved ${dataList[position].oppDisplayName ?: dataList[position].uName}'s post."
            }else if (action.contains("share")){
                "${dataList[position].displayName ?: dataList[position].userNameFromId} has shared ${dataList[position].oppDisplayName ?: dataList[position].uName}'s post."
            }else{
                ""
            }
            holder.activityItemBinding.textView67.text=desc
        } else if (dataList[position].tbl.equals("Impluse")){
            val action=dataList[position].tblAction
            val desc=if(action.contains("like",true)){
                "${dataList[position].displayName ?: dataList[position].userNameFromId} has liked ${dataList[position].oppDisplayName ?: dataList[position].uName}'s impulse."
            }else if (action.contains("comment",true)){
                "${dataList[position].displayName ?: dataList[position].userNameFromId} has commented on ${dataList[position].oppDisplayName ?: dataList[position].uName}'s impulse."
            }else if (action.contains("save",true)){
                "${dataList[position].displayName ?: dataList[position].userNameFromId} has saved ${dataList[position].oppDisplayName ?: dataList[position].uName}'s impulse."
            }else if (action.contains("share",true)){
                "${dataList[position].displayName ?: dataList[position].userNameFromId} has shared ${dataList[position].oppDisplayName ?: dataList[position].uName}'s impulse."
            }else{
                ""
            }
            holder.activityItemBinding.textView67.text=desc
        } else if (dataList[position].tbl.equals("Event")){
            val action=dataList[position].tblAction
            val desc=if(action.contains("like",true)){
                "${dataList[position].displayName ?: dataList[position].userNameFromId} has liked ${dataList[position].oppDisplayName ?: dataList[position].uName}'s event."
            }else if (action.contains("comment",true)){
                "${dataList[position].displayName ?: dataList[position].userNameFromId} has commented on ${dataList[position].oppDisplayName ?: dataList[position].uName}'s event."
            }else if (action.contains("save",true)){
                "${dataList[position].displayName ?: dataList[position].userNameFromId} has saved ${dataList[position].oppDisplayName ?: dataList[position].uName}'s event."
            }else if (action.contains("share",true)){
                "${dataList[position].displayName ?: dataList[position].userNameFromId} has shared ${dataList[position].oppDisplayName ?: dataList[position].uName}'s event."
            }else{
                ""
            }
            holder.activityItemBinding.textView67.text=desc
        }




            if (dataList[position].tbl.equals("POST") && dataList[position].type.equals("1") && dataList[position].files != null){
                holder.activityItemBinding.imageView36.visibility= View.VISIBLE
                val file=dataList[position].files.split(",")[0]
            Glide.with(holder.activityItemBinding.imageView36).load(BASE_IMAGE+file).error(R.drawable.ic_photo).into(holder.activityItemBinding.imageView36)
                if (isVideo(file)){
                    holder.activityItemBinding.imageButton45.visibility=View.VISIBLE
                }else{
                    holder.activityItemBinding.imageButton45.visibility=View.GONE
                }
            }else{
                holder.activityItemBinding.imageView36.visibility= View.GONE
            }

            if (dataList[position].tbl.equals("Following")){
                holder.activityItemBinding.textView67.text="${dataList[position].displayName ?: dataList[position].userNameFromId} following you."
            }

            holder.activityItemBinding.imageButton45.setOnClickListener { v->
                listener.onShowVidImage(dataList[position])
            }
            holder.activityItemBinding.imageView36.setOnClickListener { v->
                listener.onShowVidImage(dataList[position])
            }
            holder.activityItemBinding.circleImageView6.setOnClickListener { v->
               // listener.onActivityClick(dataList[position])
            }
            holder.activityItemBinding.textView65.setOnClickListener { v->
               // listener.onActivityClick(dataList[position])
            }
    }

    override fun getItemCount() = dataList.size

    interface FollowingActivityClickListener{
        fun onActivityClick(activitiesData: ActivitiesData)
        fun onShowVidImage(activitiesData: ActivitiesData)
    }
}