package com.wiesoftware.spine.ui.home.menus.events.event_details

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.wiesoftware.spine.R
import com.wiesoftware.spine.data.net.reponses.ImageData
import com.wiesoftware.spine.databinding.SliderImgItemBinding

/**
 * Created by Vivek kumar on 1/11/2021.
 * E-mail:- vivekpcst.kumar@gmail.com
 */
class EventsDetailsImageAdapter(val imgList: List<ImageData>) :
    RecyclerView.Adapter<EventsDetailsImageAdapter.ImageHolder>() {
    class ImageHolder(
        val sliderImgItemBinding: SliderImgItemBinding
    ) :
        RecyclerView.ViewHolder(sliderImgItemBinding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ImageHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.slider_img_item,
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: ImageHolder, position: Int) {
        Log.e("listnidhi", imgList[position].toString())
        holder.sliderImgItemBinding.model = imgList[position]
    }

    override fun getItemCount() = imgList.size
}