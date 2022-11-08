package com.wiesoftware.spine.ui.home.menus.spine.practicioners

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.CompoundButton
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.wiesoftware.spine.R
import com.wiesoftware.spine.data.net.reponses.EventCatData
import com.wiesoftware.spine.databinding.EveCatItemBinding
import kotlinx.android.synthetic.main.eve_catmuti_item.view.*
import kotlinx.android.synthetic.main.eve_catmuti_item.view.checkBox
import kotlinx.android.synthetic.main.pract_catmuti_item.view.*
import java.util.*

/**
 * Created by Vivek kumar on 1/14/2021.
 * E-mail:- vivekpcst.kumar@gmail.com
 */
class PractCatAdapter(
    val list: List<EventCatData>,
    val listener: OnEveItemChecked,
) : RecyclerView.Adapter<PractCatAdapter.CatHolder>() {


    inner class CatHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var checkbox: CheckBox = itemView.checkBox
    }


    interface OnEveItemChecked {
        fun onEventItemChecked(eveCataData: EventCatData, b: Boolean)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CatHolder {
        var view =
            LayoutInflater.from(parent.context).inflate(R.layout.pract_catmuti_item, parent, false)
        return CatHolder(view);
    }

    override fun onBindViewHolder(holder: CatHolder, position: Int) {
        var model = list[position]

        holder.checkbox.text = model.category_name

        holder.checkbox.isChecked = model.isSelect

        holder.checkbox.setOnCheckedChangeListener { compoundButton, b ->
            model.isSelect = b

        }

    }


    override fun getItemCount() = list.size

}