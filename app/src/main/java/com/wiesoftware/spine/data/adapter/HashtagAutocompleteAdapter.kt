package com.wiesoftware.spine.data.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.wiesoftware.spine.R
import com.wiesoftware.spine.data.net.reponses.EventsData
import com.wiesoftware.spine.data.net.reponses.HashtagData
import com.wiesoftware.spine.databinding.HashtagAutoItemBinding
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by Vivek kumar on 3/12/2021.
 * Email: vivekpcst.kumar@gmail.com.
 */
class HashtagAutocompleteAdapter(val dataList: MutableList<HashtagData>, val listener: OnHashtagSelectedListener): RecyclerView.Adapter<HashtagAutocompleteAdapter.HashtagHolder>(), Filterable {
    class HashtagHolder(val hashtagAutoItemBinding: HashtagAutoItemBinding): RecyclerView.ViewHolder(hashtagAutoItemBinding.root) {

    }


    var filterDataList: List<HashtagData> = ArrayList<HashtagData>()
    init {
        filterDataList=dataList
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = HashtagHolder(
        DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.hashtag_auto_item,
            parent,
            false
        )
    )

    override fun onBindViewHolder(holder: HashtagHolder, position: Int) {
        holder.hashtagAutoItemBinding.model=filterDataList[position]
        holder.hashtagAutoItemBinding.textView268.setOnClickListener {
            listener.onHashtagSelected(filterDataList[position])
        }
        /*temp code*/
        holder.hashtagAutoItemBinding.textView268.text=filterDataList[position].hash_title
        holder.hashtagAutoItemBinding.textView271.text=filterDataList[position].totalCount
    }

    override fun getItemCount() = filterDataList.size

    interface OnHashtagSelectedListener{
        fun onHashtagSelected(hashtagData: HashtagData)
    }

    override fun getFilter(): Filter {


        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                if (charSearch.isEmpty()) {
                    filterDataList = dataList
                } else {
                    val resultList = ArrayList<HashtagData>()
                    for (row in dataList) {
                            if ((row.hash_title).toLowerCase(Locale.ROOT).contains(charSearch.toLowerCase(Locale.ROOT))) {
                                resultList.add(row)
                            }

                    }
                    filterDataList = resultList
                }
                val filterResults = FilterResults()
                filterResults.values = filterDataList
                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filterDataList = results?.values as ArrayList<HashtagData>
                notifyDataSetChanged()
                Log.e("filteredListSize:",""+filterDataList.size)
            }
        }
    }

}