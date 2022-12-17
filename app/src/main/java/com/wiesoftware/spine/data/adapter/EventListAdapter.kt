package com.wiesoftware.spine.data.adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wiesoftware.spine.R
import com.wiesoftware.spine.data.net.reponses.EventsData
import com.wiesoftware.spine.data.net.reponses.EventsRecord
import com.wiesoftware.spine.databinding.RvEventListItemBinding
import com.wiesoftware.spine.ui.home.menus.events.filter.FilterEventActivity
import kotlinx.android.synthetic.main.rv_event_list_item.view.*
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


/**
 * Created by Vivek kumar on 9/10/2020.
 * E-mail:- vivekpcst.kumar@gmail.com
 */
class EventListAdapter(
    val requireContext: Context,
    var dataList: List<EventsData>,
    val listener: OnEventSaveListener,
    val value: Int,
) : RecyclerView.Adapter<EventListAdapter.EventContentHolder>(),
    EventContentAdapter.SaveEventListener, Filterable {

    var filterDataList: List<EventsData> = ArrayList<EventsData>()

    init {
        filterDataList = dataList
    }

    class EventContentHolder(
        val rvEventListItemBinding: RvEventListItemBinding
    ) : RecyclerView.ViewHolder(rvEventListItemBinding.root) {
        val rvEventContent: RecyclerView = rvEventListItemBinding.rvEventContent
    }

    fun setFilterData(dataList: List<EventsData>) {
        filterDataList = dataList
        notifyDataSetChanged()
    }

    interface OnEventSaveListener {
        fun onEventSave(record: EventsRecord, value: Int)
        fun onEventDetails(record: EventsRecord)
        fun onEventShare(record: EventsRecord)
        fun crossEvent(value: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        EventContentHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.rv_event_list_item,
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: EventContentHolder, position: Int) {
        val eve_date = filterDataList[position].startDate
//        val eve_date = "2022-10-23"
        val simpleDateFormat = SimpleDateFormat("yyyy-M-dd", Locale.getDefault())

        try {
            val date1: Date = simpleDateFormat.parse(eve_date)
            val dd = SimpleDateFormat("EEE, dd MMM yyyy", Locale.getDefault())
            val ss: String = dd.format(date1).uppercase(Locale.getDefault())
            Log.e("fmtDate: ", ss)
            if (value == 1) {
                holder.rvEventListItemBinding.textView33.text = ss
                holder.itemView.textedit_fltr.visibility = View.VISIBLE
                holder.itemView.txt_cross.visibility = View.VISIBLE
                holder.itemView.textedit_fltr.setOnClickListener {
                    requireContext.startActivity(
                        Intent(
                            requireContext,
                            FilterEventActivity::class.java
                        )
                    )
                }
                holder.itemView.txt_cross.setOnClickListener {
                    listener.crossEvent(1)
                }
            } else {
                holder.rvEventListItemBinding.textView33.text = ss
                holder.itemView.textedit_fltr.visibility = View.GONE
                holder.itemView.txt_cross.visibility = View.GONE
            }

        } catch (e: ParseException) {
            e.printStackTrace()
            Log.e("fmtDate: ", e.message.toString())
        }
        holder.rvEventListItemBinding.model=dataList[position]
        val recordsList = filterDataList[position].records
        var adapter = EventContentAdapter(requireContext, recordsList, this)
        if (recordsList.size > 0) {
            holder.rvEventContent.also {
                it.layoutManager = LinearLayoutManager(requireContext, RecyclerView.VERTICAL, false)
                it.setHasFixedSize(true)
                it.adapter = adapter
            }
        }

    }

    override fun getItemCount() = filterDataList.size

    override fun onEventSaved(record: EventsRecord, value: Int) {
        listener.onEventSave(record, value)
    }

    override fun onEventDetails(record: EventsRecord) {
        listener.onEventDetails(record)
    }

    override fun onEventShare(record: EventsRecord) {
        listener.onEventShare(record)
    }


    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {

                val charSearch = constraint.toString()
                if (charSearch.isEmpty()) {
                    filterDataList = dataList
                } else {
                    val resultList = ArrayList<EventsData>()
                    for (row in dataList) {
                        for (data in row.records) {
                            if ((data.title).toLowerCase(Locale.ROOT)
                                    .contains(charSearch.toLowerCase(Locale.ROOT))
                                || (data.location).toLowerCase(Locale.ROOT)
                                    .contains(charSearch.toLowerCase(Locale.ROOT))
                                || (data.description).toLowerCase(Locale.ROOT)
                                    .contains(charSearch.toLowerCase(Locale.ROOT))
                            ) {
                                resultList.add(row)
                            }
                        }
                    }
                    filterDataList = resultList
                }
                val filterResults = FilterResults()
                filterResults.values = filterDataList
                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filterDataList = results?.values as ArrayList<EventsData>
                notifyDataSetChanged()
            }
        }
    }
}