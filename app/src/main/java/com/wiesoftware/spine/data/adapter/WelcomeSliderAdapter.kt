package com.wiesoftware.spine.data.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.smarteist.autoimageslider.SliderViewAdapter
import com.wiesoftware.spine.R

/**
 * Created by Vivek kumar on 4/7/2021.
 * Email: vivekpcst.kumar@gmail.com.
 */
class WelcomeSliderAdapter(val context: Context,val sliderItemList: MutableList<WelcomeSliderItem>):
    SliderViewAdapter<WelcomeSliderAdapter.WelcomeViewHolder>() {
    class WelcomeViewHolder(val itemView: View):SliderViewAdapter.ViewHolder(itemView.rootView) {
        val tvDesc=itemView.findViewById<TextView>(R.id.textView286)
        val tvTitle=itemView.findViewById<TextView>(R.id.textView287)
        fun setSliderItem(sliderItem: WelcomeSliderItem){
            tvDesc.text=sliderItem.description
            tvTitle.text=sliderItem.title
        }
    }
    fun addSliderItem(sliderItem: WelcomeSliderItem){
        sliderItemList.add(sliderItem)
        notifyDataSetChanged()
    }

    override fun getCount() = sliderItemList.size

    override fun onCreateViewHolder(parent: ViewGroup?) =
        WelcomeViewHolder(
            LayoutInflater.from(context).inflate(R.layout.welcome_slider_item,parent,false)
        )

    override fun onBindViewHolder(viewHolder: WelcomeViewHolder?, position: Int) {
        viewHolder?.setSliderItem(sliderItemList[position])
    }
}
data class WelcomeSliderItem(val description: String,val title: String)