package com.wiesoftware.spine.ui.home.menus.events.addevents

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.wiesoftware.spine.R
import com.wiesoftware.spine.data.net.reponses.EventCatData
import com.wiesoftware.spine.databinding.EveCatItemBinding
import kotlinx.android.synthetic.main.eve_catmuti_item.view.*
import java.util.*

/**
 * Created by Vivek kumar on 1/14/2021.
 * E-mail:- vivekpcst.kumar@gmail.com
 */
class MutilpeSpineCatAdapter(val list: List<EventCatData>,
                             val listener: OnEveItemChecked,
                             var listValue: ListValue): RecyclerView.Adapter<MutilpeSpineCatAdapter.CatHolder>() {

    /*class CatHolder(val eveCatItemBinding: EveCatItemBinding):RecyclerView.ViewHolder(eveCatItemBinding.root) {

    }*/
    class CatHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

    }

    val checkedItemList: MutableMap<String,Boolean> = mutableMapOf()


    interface OnEveItemChecked{
        fun onEventItemChecked(eveCataData: EventCatData, b: Boolean)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CatHolder {
        var view=LayoutInflater.from(parent.context).inflate(R.layout.eve_catmuti_item,parent,false)
        return CatHolder(view);
    }

   /* override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        CatHolder(

                LayoutInflater.from(parent.context),
                R.layout.eve_catmuti_item,
                parent,
                false

        )*/



    override fun onBindViewHolder(holder: CatHolder, position: Int) {

holder.itemView.textView132.text=list[position].category_name

      //  holder.eveCatItemBinding.viewmodel=list[position]

        var address: EventCatData =list[position]

        holder.itemView.checkBox.setOnCheckedChangeListener(null)
        if (checkedItemList.containsKey(list[position].id)){
             holder.itemView.checkBox.isChecked = checkedItemList.get(list[position].id)!!
        }else{
             holder.itemView.checkBox.isChecked = false
        }
         holder.itemView.checkBox.setOnCheckedChangeListener { compoundButton, b ->
            listener.onEventItemChecked(list[position],b)
            checkedItemList.put(list[position].id,b)

        }

        if (address.isSelect) {
             holder.itemView.checkBox.setChecked(true)
        } else {
             holder.itemView.checkBox.setChecked(false)
        }

       /*  holder.itemView.checkBox.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
            if (buttonView.isPressed) {
                for (address in list) {
                    address.isSelect=false
                }
                address.isSelect =  holder.itemView.checkBox.isChecked()
                listValue.onclick(address)

                notifyDataSetChanged()
            }
        })*/


    }

    interface  ListValue{
        fun onclick(Event:EventCatData)
    }



    override fun getItemCount() = list.size

}