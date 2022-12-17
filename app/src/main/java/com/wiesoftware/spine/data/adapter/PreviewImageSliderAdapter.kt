package com.wiesoftware.spine.data.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.smarteist.autoimageslider.SliderViewAdapter
import com.wiesoftware.spine.R
import kotlinx.android.synthetic.main.preview_img_item.view.*

/**
 * Created by Vivek kumar on 4/12/2021.
 * Email: vivekpcst.kumar@gmail.com.
 */
class PreviewImageSliderAdapter(val photoURILists: MutableList<Uri>): SliderViewAdapter<PreviewImageSliderAdapter.PreviewImageHolder>() {
    class PreviewImageHolder(val itemView: View): SliderViewAdapter.ViewHolder(itemView) {
        val imageView41=itemView.findViewById<ImageView>(R.id.imageView41)
    }

    override fun getCount() = photoURILists.size

    override fun onCreateViewHolder(parent: ViewGroup) = PreviewImageHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.preview_img_item,parent,false)
    )

    override fun onBindViewHolder(viewHolder: PreviewImageHolder, position: Int) {
        Glide.with(viewHolder.imageView41).load(photoURILists[position]).into(viewHolder.imageView41)
    }
}