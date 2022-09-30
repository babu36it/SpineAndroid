package com.wiesoftware.spine.data.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.wiesoftware.spine.R
import com.wiesoftware.spine.data.net.reponses.EventsRecord
import com.wiesoftware.spine.databinding.RvEventContentItemBinding
import com.wiesoftware.spine.util.printDifference
import com.wiesoftware.spine.util.toast
import kotlinx.android.synthetic.main.rv_event_content_item.view.*
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by Vivek kumar on 9/10/2020.
 * E-mail:- vivekpcst.kumar@gmail.com
 */
class EventContentAdapter(val requireContext: Context,
                          val recordsList: List<EventsRecord>,val listener: SaveEventListener) : RecyclerView.Adapter<EventContentAdapter.ContentHolder>() {
    class ContentHolder(
        val rvEventContentItemBinding: RvEventContentItemBinding,
        val context: Context
    ): RecyclerView.ViewHolder(rvEventContentItemBinding.root) {

    }
    var checklist=ArrayList<Int>()
    interface SaveEventListener{
        fun onEventSaved(record: EventsRecord,value:Int)
        fun onEventDetails(record: EventsRecord)
        fun onEventShare(record: EventsRecord)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ContentHolder(
            DataBindingUtil.inflate(LayoutInflater.from(parent.context),
            R.layout.rv_event_content_item,
                parent,
                false
                ),parent.context
        )

    override fun onBindViewHolder(holder: ContentHolder, position: Int) {
        holder.rvEventContentItemBinding.model=recordsList[position]
        val eveType=recordsList[position].type
        if (eveType.equals("1")){
            holder.rvEventContentItemBinding.textView201.text=holder.context.getString(R.string.online)
        }else{
            holder.rvEventContentItemBinding.textView201.text=holder.context.getString(R.string.local)
        }

        holder.rvEventContentItemBinding.circleImageView3.setOnClickListener {
            listener.onEventDetails(recordsList[position])
        }
        holder.rvEventContentItemBinding.textView36.setOnClickListener {
            listener.onEventDetails(recordsList[position])
        }
        holder.rvEventContentItemBinding.root.setOnClickListener {
            listener.onEventDetails(recordsList[position])
        }
        if (recordsList[position].userSaveStatus?.equals("1")) {
            holder.rvEventContentItemBinding.imageButton7.setImageResource(R.drawable.ic_saved)
        }else{
            holder.rvEventContentItemBinding.imageButton7.setImageResource(R.drawable.ic_bookmark)
        }
        var check:Boolean=false

        if (recordsList[position].userSaveStatus.equals("1")){
            holder.rvEventContentItemBinding.imageButton7.setOnClickListener {
                listener.onEventSaved(recordsList[position],0)
            }
        }else{
            holder.rvEventContentItemBinding.imageButton7.setOnClickListener {
                listener.onEventSaved(recordsList[position],1)
                holder.rvEventContentItemBinding.imageButton7.setImageResource(R.drawable.ic_saved)
            }
        }

        holder.rvEventContentItemBinding.imageButton8.setOnClickListener {
            listener.onEventShare(recordsList[position])
        }


        val stDate=recordsList[position].startDate
        val edDate=recordsList[position].endDate
        val cc: Date = Calendar.getInstance().getTime()
        val simpleDateFormat = SimpleDateFormat("yyyy-M-dd", Locale.getDefault())
        try {
            val s=recordsList[position].createdOn


            val date1: Date = simpleDateFormat.parse(stDate)
            val date: String = simpleDateFormat.format(cc)
            val date2: Date = simpleDateFormat.parse(edDate)
            if (date1 != null && date2 != null) {
                val d: String = printDifference(date1, date2)!!

                if (d.contains("-")){
                    holder.rvEventContentItemBinding.textView39.text=d.replace("-","")
                }else{
                    holder.rvEventContentItemBinding.textView39.text=d
                    if (d.contains("0")){
                        holder.rvEventContentItemBinding.textView39.visibility = View.GONE
                        holder.rvEventContentItemBinding.tvTimeTxt.visibility = View.VISIBLE
                    } else {
                        holder.rvEventContentItemBinding.textView39.visibility = View.VISIBLE
                        holder.rvEventContentItemBinding.tvTimeTxt.visibility = View.GONE
                    }
                }
                Log.e("Diff::","$d")
            }
        } catch (e: ParseException) {
            e.printStackTrace()
            Log.e("Diff::","$stDate, $edDate")
        }

        var formatter =  SimpleDateFormat("HH:mm:ss");
        val dTime = formatter.parse(recordsList[position].startTime)
        val dTimeone = formatter.parse(recordsList[position].endTime)
        Log.e("hou",dTime.hours.toString())

        holder.itemView.tv_time_txt.text= dTime.hours.toString() +":"+ dTime.hours +"-"+dTimeone.hours+":"+dTimeone.minutes



    }

    override fun getItemCount() = recordsList.size
}