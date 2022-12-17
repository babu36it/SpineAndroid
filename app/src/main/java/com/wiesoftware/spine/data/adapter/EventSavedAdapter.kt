package com.wiesoftware.spine.data.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.wiesoftware.spine.R
import com.wiesoftware.spine.data.net.reponses.EventsRecord
import com.wiesoftware.spine.databinding.EventSavedItemBinding

/**
 * Created by Vivek kumar on 1/21/2021.
 * Email: vivekpcst.kumar@gmail.com.
 */
class EventSavedAdapter(val dataList: List<EventsRecord>,val listener: EventItemClickListener): RecyclerView.Adapter<EventSavedAdapter.EventHolder>() {
   var c_pos=0

    class EventHolder(val eventSavedItemBinding: EventSavedItemBinding):RecyclerView.ViewHolder(eventSavedItemBinding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        EventHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.event_saved_item,
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: EventHolder, position: Int) {

        if (dataList.size >= 4) {
            holder.eventSavedItemBinding.model=dataList[position]
        }else{
            if (c_pos < dataList.size){
                holder.eventSavedItemBinding.model=dataList[position]
                c_pos++
            }
        }
        holder.eventSavedItemBinding.savedE.setOnClickListener { v->
            if(dataList.size > position) {
                listener.onEventItemClick(dataList[position])
            }
        }

    }

    override fun getItemCount() = 4

    interface EventItemClickListener{
        fun onEventItemClick(eventsRecord: EventsRecord)
    }
}