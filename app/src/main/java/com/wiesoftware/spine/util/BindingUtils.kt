package com.wiesoftware.spine.util

import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.wiesoftware.spine.R
import com.wiesoftware.spine.ui.home.menus.spine.foryou.BASE_IMAGE
import com.wiesoftware.spine.ui.home.menus.spine.foryou.POST_BASE_IMG_FILE
import com.wiesoftware.spine.ui.home.menus.spine.foryou.POST_BASE_IMG_PRO
import com.wiesoftware.spine.ui.home.menus.spine.foryou.STORY_IMAGE
import de.hdodenhof.circleimageview.CircleImageView

/**
 * Created by Vivek kumar on 8/17/2020.
 * E-mail:- vivekpcst.kumar@gmail.com
 */
var POD_FILE_BASE: String? =null



@BindingAdapter("image")
fun loadImage(view: CircleImageView,url: String?){
    if(BASE_IMAGE != null) {
        Glide.with(view)
            .load(BASE_IMAGE + url)
            .placeholder(R.drawable.ic_photo)
            .into(view)
    }
}

@BindingAdapter("img")
fun loadImg(view: CircleImageView,url: String?){
    if(STORY_IMAGE != null) {
        Glide.with(view)
            .load(STORY_IMAGE + url)
            .placeholder(R.drawable.ic_photo)
            .into(view)
    }
}



@BindingAdapter("imgPro")
fun loadImgs(view: CircleImageView,url: String?){
    if(POST_BASE_IMG_PRO != null) {
        Glide.with(view)
            .load(POST_BASE_IMG_PRO + url)
            .placeholder(R.drawable.ic_photo)
            .into(view)
    }
}



@BindingAdapter("image")
fun loadImage(view: ImageView, url: String?){

    if (BASE_IMAGE != null) {
        Log.e("basee", BASE_IMAGE.toString())
        Glide.with(view)
            .load(BASE_IMAGE + url)
            .placeholder(R.drawable.ic_photo)
            .into(view)
    }
}

@BindingAdapter("podcastthumbanail")
fun loadPodCastImage(view: ImageView, url: String?){
    if (BASE_IMAGE != null) {
        Glide.with(view)
            .load(url)
            .placeholder(R.drawable.ic_photo)
            .into(view)
    }
}

@BindingAdapter("imagePost")
fun loadImages(view: ImageView, url: String?){

    Glide.with(view)
        .load(url)
        .placeholder(R.drawable.ic_photo)
        .into(view)
}


@BindingAdapter("isVisible")
fun setIsVisible(view: View, isVisible: Boolean) {
    if (isVisible) {
        view.visibility = View.VISIBLE
    } else {
        view.visibility = View.GONE
    }
}