package com.wiesoftware.spine.data.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.wiesoftware.spine.R
import com.wiesoftware.spine.databinding.RvEventMenuItemBinding

/**
 * Created by Vivek kumar on 9/9/2020.
 * E-mail:- vivekpcst.kumar@gmail.com
 */
class EventMenuAdapter(val context: Context, val menuList: List<String>,val selected: Int,val listener: OnMenuSelectedListener): RecyclerView.Adapter<EventMenuAdapter.MenuItemHolder>() {
    class MenuItemHolder(
        rvEventMenuItemBinding: RvEventMenuItemBinding
    ): RecyclerView.ViewHolder(rvEventMenuItemBinding.root) {
        val tv: TextView=rvEventMenuItemBinding.textView28
        val txt_view: View=rvEventMenuItemBinding.txtView
        val lnr_event_list:LinearLayout=rvEventMenuItemBinding.lnrEventList
    }

    interface OnMenuSelectedListener{
        fun onMenuSelected(selected: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        MenuItemHolder(
            DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.rv_event_menu_item,
            parent,
            false
            )
        )


    override fun onBindViewHolder(holder: MenuItemHolder, position: Int) {
        holder.tv.text=menuList[position]
        if (position == selected){
            holder.txt_view.visibility=View.VISIBLE
          //  holder.cardView.setCardBackgroundColor(ContextCompat.getColor(context,R.color.colorPrimaryDark))
         //   holder.tv.setTextColor(ContextCompat.getColor(context,R.color.text_white))
        }else{
            holder.txt_view.visibility=View.GONE
         //   holder.cardView.setCardBackgroundColor(ContextCompat.getColor(context,R.color.text_white))
          //  holder.tv.setTextColor(ContextCompat.getColor(context,R.color.colorPrimaryDark))
        }
        holder.lnr_event_list.setOnClickListener {
            listener.onMenuSelected(position)
        }
    }

    override fun getItemCount() = menuList.size
}