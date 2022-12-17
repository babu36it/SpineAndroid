package com.wiesoftware.spine.data.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.smarteist.autoimageslider.SliderViewAdapter
import com.wiesoftware.spine.R

/**
 * Created by Vivek kumar on 4/6/2021.
 * Email: vivekpcst.kumar@gmail.com.
 */
class SliderPostImageAdapter(
    val context: Context,
    val listener: SliderItemClickListener,
    val vPosition: Int
):
    SliderViewAdapter<SliderPostImageAdapter.SliderImageHolder>() {
    private var imageList: MutableList<String> =  ArrayList<String>()

    class SliderImageHolder(itemView: View?) :
        SliderViewAdapter.ViewHolder(itemView) {
        val imageView40=itemView!!.findViewById<ImageView>(R.id.imageView40)
        val imageButton77=itemView!!.findViewById<ImageButton>(R.id.imageButton77)
    }

    override fun getCount() = imageList.size

    override fun onCreateViewHolder(parent: ViewGroup?) =  SliderImageHolder(
        LayoutInflater.from(context).inflate(R.layout.post_slider_img_item, parent, false)
    )

    override fun onBindViewHolder(viewHolder: SliderImageHolder?, position: Int) {
        if (isVideo(imageList[position])){
            viewHolder!!.imageButton77.visibility=View.VISIBLE
        }else{
            viewHolder!!.imageButton77.visibility=View.INVISIBLE
        }
        Glide.with(viewHolder.itemView)
            .load(imageList[position])
            .fitCenter()
            .into(viewHolder.imageView40)

        viewHolder.itemView.setOnClickListener {
            listener.onSliderItemClicked(vPosition)
        }
    }

    fun addItem(sliderItem: String) {
        this.imageList.add(sliderItem)
        notifyDataSetChanged()
    }
    fun renewItems(sliderItems: MutableList<String>) {
        imageList = sliderItems
        notifyDataSetChanged()
    }
    fun deleteItem(position: Int) {
        imageList.removeAt(position)
        notifyDataSetChanged()
    }

    fun isVideo(media_file: String) =
        media_file.contains(".mp4",true) ||
                media_file.contains(".mov",true)  ||
                media_file.contains(".3gp",true)  ||
                media_file.contains(".avi",true)


    interface SliderItemClickListener{
        fun onSliderItemClicked(vPosition: Int)
    }
}