package com.wiesoftware.spine.ui.home.menus.spine.practicioners

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.wiesoftware.spine.R
import kotlinx.android.synthetic.main.location_item.view.*

/**
 * Created by Vivek kumar on 1/14/2021.
 * E-mail:- vivekpcst.kumar@gmail.com
 */
class PractLocAdapter(
    val list: List<LocationModel>,
    val listener: OnItemChecked,
) : RecyclerView.Adapter<PractLocAdapter.CatHolder>() {


    inner class CatHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var textLocation: TextView = itemView.textLocation

    }


    interface OnItemChecked {
        fun onItemChecked(item: LocationModel)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CatHolder {
        var view =
            LayoutInflater.from(parent.context).inflate(R.layout.location_item, parent, false)
        return CatHolder(view);
    }

    override fun onBindViewHolder(holder: CatHolder, position: Int) {
        var model = list[position]
         holder.textLocation.text = model.location
        holder.itemView.setOnClickListener {
            listener.onItemChecked(model)
        }

    }


    override fun getItemCount() = list.size

}