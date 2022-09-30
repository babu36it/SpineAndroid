package com.wiesoftware.spine.ui.home.menus.spine.addposts.poststory

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.wiesoftware.spine.R
import com.wiesoftware.spine.databinding.SelectedImgItemBinding
import kotlinx.android.synthetic.main.selected_img_item.view.*

/**
 * Created by Vivek kumar on 10/30/2020.
 * E-mail:- vivekpcst.kumar@gmail.com
 */
class SelectedImageAdapter(val bitmaps: ArrayList<Bitmap>,val imageRemoveListener: ImageRemoveListener): RecyclerView.Adapter<SelectedImageAdapter.ImgHolder>() {
    class ImgHolder(val selectedImgItemBinding: SelectedImgItemBinding): RecyclerView.ViewHolder(selectedImgItemBinding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ImgHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.selected_img_item,
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: ImgHolder, position: Int) {
        if (bitmaps[position] == null){
            holder.itemView.imageView34.visibility=View.VISIBLE
        }else{
            holder.itemView.imageView34.visibility=View.GONE
        }
        holder.itemView.ivSelected.setImageBitmap(bitmaps[position])
        holder.itemView.ibDelete.setOnClickListener {
            imageRemoveListener.onImageRemoved(position)
        }
    }

    override fun getItemCount() = bitmaps.size

    interface ImageRemoveListener{
        fun onImageRemoved(position: Int)
    }

    data class SelectedImage(
        val img: Bitmap
    ){}
}