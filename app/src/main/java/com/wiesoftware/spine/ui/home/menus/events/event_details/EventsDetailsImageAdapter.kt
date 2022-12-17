package com.wiesoftware.spine.ui.home.menus.events.event_details

import android.graphics.BitmapFactory
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.wiesoftware.spine.R
import com.wiesoftware.spine.data.net.reponses.ImageData
import com.wiesoftware.spine.databinding.SliderImgItemBinding
import java.net.URL


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

        val SDK_INT = Build.VERSION.SDK_INT
        if (SDK_INT > 8) {
            val policy = ThreadPolicy.Builder()
                .permitAll().build()
            StrictMode.setThreadPolicy(policy)
            //your codes here
        }

        val circularProgressDrawable = CircularProgressDrawable(holder.itemView.context)
        circularProgressDrawable.strokeWidth = 5f
        circularProgressDrawable.centerRadius = 30f
        circularProgressDrawable.start()

        Glide.with(holder.itemView.context)
            .load(imgList[position].image)
            .centerCrop()
            .placeholder( circularProgressDrawable)
            .error( ColorDrawable(ContextCompat.getColor(holder.itemView.context, R.color.light_gry)))
            .dontAnimate()
            .into(holder.sliderImgItemBinding.imageView13);

//        val newurl = URL(imgList[position].image)
//        var mIcon_val = BitmapFactory.decodeStream(newurl.openConnection().getInputStream())
//       holder.sliderImgItemBinding.imageView13.setImageBitmap(mIcon_val)
//        holder.sliderImgItemBinding.model = imgList[position]
    }

    override fun getItemCount() = imgList.size
}