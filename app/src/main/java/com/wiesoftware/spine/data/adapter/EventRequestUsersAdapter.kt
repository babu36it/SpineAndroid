package com.wiesoftware.spine.data.adapter

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.wiesoftware.spine.R
import com.wiesoftware.spine.data.net.reponses.EventRequestData
import com.wiesoftware.spine.databinding.EventRequestUserItemBinding

/**
 * Created by Vivek kumar on 2/3/2021.
 * Email: vivekpcst.kumar@gmail.com.
 */
class EventRequestUsersAdapter(val dataList: List<EventRequestData>,val listner:EventRequestUserListener): RecyclerView.Adapter<EventRequestUsersAdapter.UserHolder>() {
    class UserHolder(val eventRequestUserItemBinding: EventRequestUserItemBinding,val context: Context): RecyclerView.ViewHolder(eventRequestUserItemBinding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        UserHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.event_request_user_item,
                parent,
                false
            ),
            parent.context
        )

    override fun onBindViewHolder(holder: UserHolder, position: Int) {
       holder.eventRequestUserItemBinding.viewmodel=dataList[position]
        val eveRequestData: EventRequestData=dataList[position]
        val bookingStatus: String=eveRequestData.booking_status
        bookingStatus.let {
            if (bookingStatus.equals("0")){
                holder.eventRequestUserItemBinding.imageButton37.setImageResource(R.drawable.ic_check)
                holder.eventRequestUserItemBinding.imageButton38.setImageResource(R.drawable.ic_close)
            }else if (bookingStatus.equals("2")){
//                holder.eventRequestUserItemBinding.imageButton37.setImageResource(R.drawable.ic_check_circle)
                holder.eventRequestUserItemBinding.imageButton37.setBackgroundResource(R.drawable.circle_fill)
                holder.eventRequestUserItemBinding.imageButton37.setColorFilter(Color.argb(255, 255, 255, 255));
//                holder.eventRequestUserItemBinding.imageButton38.setImageResource(R.drawable.ic_close)
            }else if (bookingStatus.equals("3")){
                holder.eventRequestUserItemBinding.imageButton37.setImageResource(R.drawable.ic_check)
//               holder.eventRequestUserItemBinding.imageButton38.setImageResource(R.drawable.ic_close_circle)

            }
        }
        holder.eventRequestUserItemBinding.imageButton37.setOnClickListener {
            listner.onAcceptRequest(dataList[position])
            holder.eventRequestUserItemBinding.imageButton37.setImageResource(R.drawable.ic_check_circle)
            holder.eventRequestUserItemBinding.imageButton38.setImageResource(R.drawable.ic_close)
        }
        holder.eventRequestUserItemBinding.imageButton38.setOnClickListener {
            listner.onDeclienedRequest(dataList[position])
            holder.eventRequestUserItemBinding.imageButton37.setImageResource(R.drawable.ic_check)
            holder.eventRequestUserItemBinding.imageButton38.setImageResource(R.drawable.ic_close_circle)

        }

    }

    override fun getItemCount() = dataList.size


    interface EventRequestUserListener{
        fun onAcceptRequest(eveRequestData: EventRequestData)
        fun onDeclienedRequest(eveRequestData: EventRequestData)
    }

}