package com.wiesoftware.spine.ui.home.menus.events.addevents

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.wiesoftware.spine.R
import com.wiesoftware.spine.data.net.reponses.EventCatData
import com.wiesoftware.spine.databinding.EveCatItemBinding
import java.util.*

/**
 * Created by Vivek kumar on 1/14/2021.
 * E-mail:- vivekpcst.kumar@gmail.com
 */
class SpinerCatAdapter(
    var list: List<EventCatData>, val listener: OnEveItemChecked,
    var listValue: ListValue): RecyclerView.Adapter<SpinerCatAdapter.CatHolder>() {
    class CatHolder(val eveCatItemBinding: EveCatItemBinding):RecyclerView.ViewHolder(eveCatItemBinding.root) {

    }

    val checkedItemList: MutableMap<String,Boolean> = mutableMapOf()


    interface OnEveItemChecked{
        fun onEventItemChecked(eveCataData: EventCatData, b: Boolean)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        CatHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.eve_cat_item,
                parent,
                false
            )
        )

    fun filterList(filterlist: List<EventCatData>) {
        // below line is to add our filtered
        // list in our course array list.
        list = filterlist
        // below line is to notify our adapter
        // as change in recycler view data.
        notifyDataSetChanged()
    }


    override fun onBindViewHolder(holder: CatHolder, position: Int) {



        holder.eveCatItemBinding.viewmodel=list[position]

        var address: EventCatData =list[position]

       /* holder.eveCatItemBinding.checkBox.setOnCheckedChangeListener(null)
        if (checkedItemList.containsKey(list[position].id)){
            holder.eveCatItemBinding.checkBox.isChecked = checkedItemList.get(list[position].id)!!
        }else{
            holder.eveCatItemBinding.checkBox.isChecked = false
        }
        holder.eveCatItemBinding.checkBox.setOnCheckedChangeListener { compoundButton, b ->
            listener.onEventItemChecked(list[position],b)
            checkedItemList.put(list[position].id,b)

        }*/

        if (address.isSelect) {
            holder.eveCatItemBinding.checkBox.setChecked(true)
        } else {
            holder.eveCatItemBinding.checkBox.setChecked(false)
        }

        holder.eveCatItemBinding.checkBox.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
            if (buttonView.isPressed) {
                for (address in list) {
                    address.isSelect=false
                }
                address.isSelect = holder.eveCatItemBinding.checkBox.isChecked()
                listValue.onclick(address)

                notifyDataSetChanged()
            }
        })


    }

    interface  ListValue{
        fun onclick(Event:EventCatData)
    }



    override fun getItemCount() = list.size
}