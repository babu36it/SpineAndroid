package com.wiesoftware.spine.ui.home.menus.events.preview_event

import android.graphics.BitmapFactory
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.wiesoftware.spine.R
import com.wiesoftware.spine.data.net.reponses.ImageData
import com.wiesoftware.spine.databinding.PreviewImgItemBinding
import com.wiesoftware.spine.databinding.SliderImgItemBinding
import java.io.IOException

/**
 * Created by Vivek kumar on 1/11/2021.
 * E-mail:- vivekpcst.kumar@gmail.com
 */
class PreviewEventImageAdapter(val imgList: List<ImageData>) :
    RecyclerView.Adapter<PreviewEventImageAdapter.ImageHolder>() {
    class ImageHolder(
        val sliderImgItemBinding: PreviewImgItemBinding
    ) :
        RecyclerView.ViewHolder(sliderImgItemBinding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ImageHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.preview_img_item,
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: ImageHolder, position: Int) {
//        holder.sliderImgItemBinding.model = imgList[position]

        try {
            val mImageBitmap =
                BitmapFactory.decodeFile(imgList[position].image!!) //MediaStore.Images.Media.getBitmap(this.contentResolver,Uri.parse(currentPhotoPath))
            holder.sliderImgItemBinding.imageView41.setImageBitmap(mImageBitmap)
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    override fun getItemCount() = imgList.size
}