package com.wiesoftware.spine.ui.home.menus.spine.practicioners

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.wiesoftware.spine.R

class PractSelectlistAdapter(var context: Context) : RecyclerView.Adapter<PractSelectlistAdapter.ListHolder>() {


   inner class ListHolder( itemView:View ) : RecyclerView.ViewHolder(itemView) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListHolder {
        var view =
            LayoutInflater.from(parent.context).inflate(R.layout.pract_selected_result_listitem, parent, false)
        return ListHolder(view);
    }

    override fun onBindViewHolder(holder: ListHolder, position: Int) {

    }

    override fun getItemCount(): Int {
      return  10
    }
}