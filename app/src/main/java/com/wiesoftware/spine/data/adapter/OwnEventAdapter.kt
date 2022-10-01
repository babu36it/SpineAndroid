package com.wiesoftware.spine.data.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.wiesoftware.spine.R
import com.wiesoftware.spine.data.net.reponses.EventsRecord
import com.wiesoftware.spine.data.net.reponses.OwnEventsData
import com.wiesoftware.spine.databinding.OwnEventItemBinding
import com.wiesoftware.spine.ui.home.menus.spine.viewmedia.ViewMediaInLargeActivity
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Vivek kumar on 1/18/2021.
 * E-mail:- vivekpcst.kumar@gmail.com
 */
class OwnEventAdapter(val  data: List<EventsRecord>, val listener: OnEventDetailsListener,
): RecyclerView.Adapter<OwnEventAdapter.OwnEventHolder>() {
    class OwnEventHolder(val ownEventItemBinding: OwnEventItemBinding, val context: Context): RecyclerView.ViewHolder(ownEventItemBinding.root) {

    }
    interface OnEventDetailsListener{
        fun onEventDetails(ownEventData: EventsRecord)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        OwnEventHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.own_event_item,
                parent,
                false
            ),parent.context
        )

    override fun onBindViewHolder(holder: OwnEventHolder, position: Int) {

        try {
            val eve_date="2022-4-27"
//            val eve_date=data[position].startDate
            val simpleDateFormat = SimpleDateFormat("yyyy-M-dd", Locale.getDefault())
            val date1: Date = simpleDateFormat.parse(eve_date)
            val dd= SimpleDateFormat("EEE, dd MMM yyyy", Locale.getDefault())
            val ss:String=dd.format(date1)
            val newDay=(ss.split(",")[1]).split(" ")[1]
            val newMonth=(ss.split(",")[1]).split(" ")[2]
            holder.ownEventItemBinding.textViewDay.text=newDay
            holder.ownEventItemBinding.textViewMonth.text=newMonth
        } catch (e: ParseException) {
            e.printStackTrace()
            Log.e("fmtDate: ",e.message.toString())
        }

        Glide.with(holder.context).load("https://www.cvent.com/sites/default/files/migrated_attachments/new_statesman_events.jpg").placeholder(R.drawable.demo).into(
            holder.ownEventItemBinding.imgEvent)


//        val type=data[position].type
        val type="0"
        if (type.equals("0")){
            holder.ownEventItemBinding.textView138.text=holder.context.getString(R.string.online)
            holder.ownEventItemBinding.textView137.text="Sahaja Yoga Online Meditation"
            holder.ownEventItemBinding.textView136.text = "18:00 | 2hrs"
        }else{
            holder.ownEventItemBinding.textView138.text=holder.context.getString(R.string.local)

        }

        if(position == 1){

            val type="1"
            if (type.equals("1")){
                holder.ownEventItemBinding.textView138.text=""

                holder.ownEventItemBinding.textView137.text="harsh Online Meditation"
                holder.ownEventItemBinding.textView136.text = "Ahemdabad | 2 days"
                holder.ownEventItemBinding.textView138.visibility = View.GONE
                holder.ownEventItemBinding.textView136.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_location, 0, 0, 0);
            }else{

            }

        }



//        holder.ownEventItemBinding.model=data[position]
//        holder.ownEventItemBinding.root.setOnClickListener {
//            listener.onEventDetails(data[position])
//        }
    }


    override fun getItemCount() = 2
}