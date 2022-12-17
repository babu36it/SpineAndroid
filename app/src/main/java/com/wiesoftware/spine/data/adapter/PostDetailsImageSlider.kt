package com.wiesoftware.spine.data.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.smarteist.autoimageslider.SliderViewAdapter
import com.wiesoftware.spine.R

/**
 * Created by Vivek kumar on 4/12/2021.
 * Email: vivekpcst.kumar@gmail.com.
 */
class PostDetailsImageSlider(val imageList: MutableList<String>):SliderViewAdapter<PostDetailsImageSlider.PostImageHolder>() {
    class PostImageHolder(itemView: View): SliderViewAdapter.ViewHolder(itemView) {
        val imageView41=itemView.findViewById<ImageView>(R.id.imageView41)
    }

    override fun getCount() = imageList.size

    override fun onCreateViewHolder(parent: ViewGroup) = PostImageHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.preview_img_item,parent,false)
    )

    override fun onBindViewHolder(viewHolder: PostImageHolder, position: Int) {
        Glide.with(viewHolder.imageView41).load(imageList[position]).placeholder(R.drawable.ic_photo).into(viewHolder.imageView41)
    }
}