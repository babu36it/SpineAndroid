package com.wiesoftware.spine.data.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.wiesoftware.spine.R
import com.wiesoftware.spine.data.net.reponses.LangData
import com.wiesoftware.spine.ui.home.menus.events.addevents.MutilpeSpineCatAdapter
import kotlinx.android.synthetic.main.eve_catmuti_item.view.*

class MultiLanguageAdapter(
    val list: List<LangData>,
    val listener: OnLanguageItemChecked,
    var listValue: ListValue
) : RecyclerView.Adapter<MultiLanguageAdapter.LanguageHolder>() {

    /*class CatHolder(val eveCatItemBinding: EveCatItemBinding):RecyclerView.ViewHolder(eveCatItemBinding.root) {

    }*/
    class LanguageHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }

    val checkedItemList: MutableMap<String, Boolean> = mutableMapOf()


    interface OnLanguageItemChecked {
        fun onLanguageItemChecked(eveCataData: LangData, b: Boolean)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LanguageHolder {
        var view =
            LayoutInflater.from(parent.context).inflate(R.layout.eve_catmuti_item, parent, false)
        return LanguageHolder(view);
    }

    /* override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
         CatHolder(

                 LayoutInflater.from(parent.context),
                 R.layout.eve_catmuti_item,
                 parent,
                 false

         )*/


    override fun onBindViewHolder(holder: LanguageHolder, position: Int) {

        holder.itemView.textView132.text = list[position].name

        //  holder.eveCatItemBinding.viewmodel=list[position]

        var address: LangData = list[position]

        holder.itemView.checkBox.setOnCheckedChangeListener(null)
        if (checkedItemList.containsKey(list[position].id)) {
            holder.itemView.checkBox.isChecked = checkedItemList.get(list[position].id)!!
        } else {
            holder.itemView.checkBox.isChecked = false
        }
        holder.itemView.checkBox.setOnCheckedChangeListener { compoundButton, b ->
            listener.onLanguageItemChecked(list[position], b)
            checkedItemList.put(list[position].id, b)

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

    interface ListValue {
        fun onLanguageClick(lang: LangData)
    }


    override fun getItemCount() = list.size

}