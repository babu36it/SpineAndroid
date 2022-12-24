package com.wiesoftware.spine.util

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.wiesoftware.spine.data.net.reponses.EventsData

class BaseAdapter<T : Any>(var context: Context) : RecyclerView.Adapter<BaseViewHolder<T>>(){
    var listOfItems:List<T>? = mutableListOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }
    var expressionViewHolderBinding: ((T, ViewBinding,Context) -> Unit)? = null
    var expressionOnCreateViewHolder:((ViewGroup)->ViewBinding)? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<T> {
        return expressionOnCreateViewHolder?.let { it(parent) }?.let { BaseViewHolder(it, expressionViewHolderBinding!!) }!!
    }

    override fun onBindViewHolder(holder: BaseViewHolder<T>, position: Int) {
        holder.bind(listOfItems!![position],context)
    }

    override fun getItemCount(): Int {
        return listOfItems!!.size
    }

    override fun getItemViewType(position: Int): Int {
        return super.getItemViewType(position)
    }
    fun setFilterData(dataList: List<T>) {
        listOfItems = dataList
        notifyDataSetChanged()
    }

}