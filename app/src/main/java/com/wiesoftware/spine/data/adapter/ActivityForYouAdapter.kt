package com.wiesoftware.spine.data.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.wiesoftware.spine.R
import com.wiesoftware.spine.data.net.reponses.ActivitiesData
import com.wiesoftware.spine.databinding.ActivityItemBinding
import com.wiesoftware.spine.ui.home.menus.activities.following.FollowingActivityAdapter
import com.wiesoftware.spine.ui.home.menus.activities.following.FollowingActivityFragment
import com.wiesoftware.spine.ui.home.menus.spine.foryou.BASE_IMAGE

/**
 * Created by Vivek kumar on 11/27/2020.
 * E-mail:- vivekpcst.kumar@gmail.com
 */
class ActivityForYouAdapter(val dataList: List<ActivitiesData>,val listener: FollowingActivityAdapter.FollowingActivityClickListener,
val valuePass:Int) : RecyclerView.Adapter<ActivityForYouAdapter.ActivitiesHolder>() {
    class ActivitiesHolder(
        var activityItemBinding: ActivityItemBinding
    ):RecyclerView.ViewHolder(activityItemBinding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ActivitiesHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.activity_item,
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: ActivitiesHolder, position: Int) {

        holder.activityItemBinding.model=dataList.get(position)

        if (valuePass==0){
            holder.activityItemBinding.textView65.text = dataList[position].userNameFromId ?: dataList[position].displayName
            holder.activityItemBinding.textView66.text=dataList[position].createdOnTimeAgo
            if (dataList[position].tbl.equals("POST")){
                val action=dataList[position].tblAction
                val desc=if(action.contains("like",true)){
                   // "${dataList[position].userNameFromId ?: dataList[position].displayName}You liked post of ${dataList[position].oppDisplayName ?: dataList[position].uName}"
                    "${dataList[position].userNameFromId ?: dataList[position].displayName} liked post of you"
                   // "${dataList[position].oppDisplayName ?: dataList[position].uName} has liked your post."
                }else if (action.contains("comment")){
                 //   "You commented post of ${dataList[position].oppDisplayName ?: dataList[position].uName}"
                    "${dataList[position].userNameFromId ?: dataList[position].displayName} comment post of you"
                  //  "${dataList[position].oppDisplayName ?: dataList[position].uName} has commented on your post."
                }else if (action.contains("save")){
                   // "You saved post of ${dataList[position].oppDisplayName ?: dataList[position].uName}"
                    "${dataList[position].userNameFromId ?: dataList[position].displayName} saved post of you"
                    //"${dataList[position].oppDisplayName ?: dataList[position].uName} has saved your post."
                }else if (action.contains("share")){
                   // "You shared post of ${dataList[position].oppDisplayName ?: dataList[position].uName}"
                    "${dataList[position].userNameFromId ?: dataList[position].displayName} shared post of you"
                  //  "${dataList[position].oppDisplayName ?: dataList[position].uName} has shared your post."
                }else{
                    ""
                }
                holder.activityItemBinding.textView67.text=desc
            }
            else if (dataList[position].tbl.equals("Impluse")){
                val action=dataList[position].tblAction
                val desc=if(action.contains("like",true)){
                 //   "You liked impulse of ${dataList[position].oppDisplayName ?: dataList[position].uName}"
                    "${dataList[position].userNameFromId ?: dataList[position].displayName} like Impluse of you"
                    //"${dataList[position].oppDisplayName ?: dataList[position].uName} has liked impulse."
                }else if (action.contains("comment",true)){
                  //  "You commented impulse of ${dataList[position].oppDisplayName ?: dataList[position].uName}"
                    "${dataList[position].userNameFromId ?: dataList[position].displayName} comment Impluse of you"
                   // "${dataList[position].oppDisplayName ?: dataList[position].uName} has commented impulse."
                }else if (action.contains("save",true)){
                  //  "You saved impulse of ${dataList[position].oppDisplayName ?: dataList[position].uName}"
                    "${dataList[position].userNameFromId ?: dataList[position].displayName} saved Impluse of you"
                //    "${dataList[position].oppDisplayName ?: dataList[position].uName} has saved impulse."
                }else if (action.contains("share",true)){
                   // "You shared impulse of ${dataList[position].oppDisplayName ?: dataList[position].uName}"
                    "${dataList[position].userNameFromId ?: dataList[position].displayName} shared Impluse of you"
                  //  "${dataList[position].oppDisplayName ?: dataList[position].uName} has shared impulse."
                }else{
                    ""
                }
                holder.activityItemBinding.textView67.text=desc
            }
            else if (dataList[position].tbl.equals("Event")){
                val action=dataList[position].tblAction
                val desc=if(action.contains("like",true)){
                 //   "You liked event of ${dataList[position].oppDisplayName ?: dataList[position].uName}"
                    "${dataList[position].userNameFromId ?: dataList[position].displayName} like Event of you"
                 //   "${dataList[position].oppDisplayName ?: dataList[position].uName} has liked your event."
                }else if (action.contains("comment",true)){
                    //"You commented event of ${dataList[position].oppDisplayName ?: dataList[position].uName}"
                    "${dataList[position].userNameFromId ?: dataList[position].displayName} commented Event of you"
                  //  "${dataList[position].oppDisplayName ?: dataList[position].uName} has commented on your event."
                }else if (action.contains("save",true)){
                  //  "You saved event of ${dataList[position].oppDisplayName ?: dataList[position].uName}"
                    "${dataList[position].userNameFromId ?: dataList[position].displayName} saved Event of you"
                  //  "${dataList[position].oppDisplayName ?: dataList[position].uName} has saved your event."
                }else if (action.contains("share",true)){
                  //  "You shared event of ${dataList[position].oppDisplayName ?: dataList[position].uName}"
                    "${dataList[position].userNameFromId ?: dataList[position].displayName} shared Event of you"
                   // "${dataList[position].oppDisplayName ?: dataList[position].uName} has shared your event."
                }else{
                    ""
                }
                holder.activityItemBinding.textView67.text=desc
            }
        }

        /*holder.activityItemBinding.textView65.text = dataList[position].oppDisplayName ?: dataList[position].uName
        if (dataList[position].tbl.equals("POST")){
            val action=dataList[position].tblAction
            val desc=if(action.contains("like",true)){
                "${dataList[position].oppDisplayName ?: dataList[position].uName} has liked your post."
            }else if (action.contains("comment")){
                "${dataList[position].oppDisplayName ?: dataList[position].uName} has commented on your post."
            }else if (action.contains("save")){
                "${dataList[position].oppDisplayName ?: dataList[position].uName} has saved your post."
            }else if (action.contains("share")){
                "${dataList[position].oppDisplayName ?: dataList[position].uName} has shared your post."
            }else{
                ""
            }
            holder.activityItemBinding.textView67.text=desc
            }
        else if (dataList[position].tbl.equals("Impluse")){
            val action=dataList[position].tblAction
            val desc=if(action.contains("like",true)){
                "${dataList[position].oppDisplayName ?: dataList[position].uName} has liked impulse."
            }else if (action.contains("comment",true)){
                "${dataList[position].oppDisplayName ?: dataList[position].uName} has commented impulse."
            }else if (action.contains("save",true)){
                "${dataList[position].oppDisplayName ?: dataList[position].uName} has saved impulse."
            }else if (action.contains("share",true)){
                "${dataList[position].oppDisplayName ?: dataList[position].uName} has shared impulse."
            }else{
                ""
            }
            holder.activityItemBinding.textView67.text=desc
        }
        else if (dataList[position].tbl.equals("Event")){
            val action=dataList[position].tblAction
            val desc=if(action.contains("like",true)){
                "${dataList[position].oppDisplayName ?: dataList[position].uName} has liked your event."
            }else if (action.contains("comment",true)){
                "${dataList[position].oppDisplayName ?: dataList[position].uName} has commented on your event."
            }else if (action.contains("save",true)){
                "${dataList[position].oppDisplayName ?: dataList[position].uName} has saved your event."
            }else if (action.contains("share",true)){
                "${dataList[position].oppDisplayName ?: dataList[position].uName} has shared your event."
            }else{
                ""
            }
            holder.activityItemBinding.textView67.text=desc
        }*/








        if (dataList[position].tbl.equals("POST") && dataList[position].type.equals("1") && dataList[position].files != null){
            holder.activityItemBinding.imageView36.visibility= View.VISIBLE
            val file=dataList[position].files.split(",")[0]
            Glide.with(holder.activityItemBinding.imageView36).load(BASE_IMAGE +file).error(R.drawable.ic_photo).into(holder.activityItemBinding.imageView36)
            if (FollowingActivityFragment.isVideo(file)){
                holder.activityItemBinding.imageButton45.visibility= View.VISIBLE
            }else{
                holder.activityItemBinding.imageButton45.visibility= View.GONE
            }
        }else{
            holder.activityItemBinding.imageView36.visibility= View.GONE
        }

        if (dataList[position].tbl.equals("Following")){
            Log.e("dataaanidhi","${dataList[position].userNameFromId}")
            holder.activityItemBinding.textView67.text="You are following ${dataList[position].oppDisplayName ?: dataList[position].uName }"
        }

        holder.activityItemBinding.imageButton45.setOnClickListener { v->
            listener.onShowVidImage(dataList[position])
        }
        holder.activityItemBinding.imageView36.setOnClickListener { v->
            listener.onShowVidImage(dataList[position])
        }
        holder.activityItemBinding.circleImageView6.setOnClickListener { v->
          //  listener.onActivityClick(dataList[position])
        }
        holder.activityItemBinding.textView65.setOnClickListener { v->
          //  listener.onActivityClick(dataList[position])
        }
    }

    override fun getItemCount()= dataList.size
}