package com.wiesoftware.spine.ui.home.menus.events.addordup

import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.wiesoftware.spine.R
import com.wiesoftware.spine.data.net.reponses.EventsRecord
import com.wiesoftware.spine.databinding.DupEventListItemBinding
import com.wiesoftware.spine.util.Utils
import kotlinx.android.synthetic.main.dup_event_list_item.view.*
import java.time.LocalDate
import java.time.Month
import java.time.format.DateTimeFormatter
import java.util.*

/**
 * Created by Vivek kumar on 6/7/2021.
 * E-mail:- vivekpcst.kumar@gmail.com
 */
class DupEventAdapter(
    val data: MutableList<EventsRecord>,
    val dupEveEventListener: DupEveEventListener
) : RecyclerView.Adapter<DupEventAdapter.DupEventHolder>()  {
    class DupEventHolder(val dupEventListItemBinding: DupEventListItemBinding): RecyclerView.ViewHolder(dupEventListItemBinding.root) {

    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = DupEventHolder(
        DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.dup_event_list_item,
            parent,
            false
        )
    )

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: DupEventHolder, position: Int) {



        try {
            holder.dupEventListItemBinding.model = data[position]
            holder.dupEventListItemBinding.root.setOnClickListener {
                dupEveEventListener.onDupEventClick(data[position])
            }
            var startdate=data[position].startDate
            var enddate=data[position].endDate

            var startvalue= Utils.dateTimeformat(startdate)
            var endValue=Utils.dateTimeformat(enddate)


            holder.itemView.textView326.text= startvalue.dayOfMonth.toString()+" "+startvalue.month+" - "+
                    endValue.dayOfMonth.toString()+" "+endValue.month + " " +startvalue.year.toString() + ", " + Utils.dateTimeformat(startvalue)


            if (data[position].type == "0") {
                holder.itemView.textView324.text = "LOCAL EVENT"
                holder.itemView.textView327.visibility = View.GONE
            } else if (data[position].type == "1") {
                holder.itemView.textView324.text = "Online Event"
                holder.itemView.textView327.visibility = View.GONE
            } else if(data[position].type == "2") {
                holder.itemView.textView324.text = "Retreat Event"
                holder.itemView.textView327.visibility = View.VISIBLE
            } else {
                holder.itemView.textView324.text = "Metaverse Sessions"
                holder.itemView.textView327.visibility = View.GONE
            }

        }catch (e:Exception){

            Log.d("DupEvents", "onBindViewHolder: $e")
        }




    }

    override fun getItemCount() = data.size

    interface DupEveEventListener{
        fun onDupEventClick(eveRecord: EventsRecord)
    }
}