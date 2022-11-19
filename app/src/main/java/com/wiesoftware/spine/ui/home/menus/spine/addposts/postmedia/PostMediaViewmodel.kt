package com.wiesoftware.spine.ui.home.menus.spine.addposts.postmedia

import android.view.View
import androidx.lifecycle.ViewModel
import com.wiesoftware.spine.data.repo.HomeRepositry

/**
 * Created by Vivek kumar on 11/5/2020.
 * E-mail:- vivekpcst.kumar@gmail.com
 */
class PostMediaViewmodel(val homeRepositry: HomeRepositry) : ViewModel() {
    var postMediaEventListener: PostMediaEventListener?=null
    var thoughts: String=""
    var hashtags: String="-"

    fun onBack(view: View){
        postMediaEventListener?.onBack()
    }
    fun onPost(view: View){
        postMediaEventListener?.onPost()
    }
    fun onAdd(view: View){
        postMediaEventListener?.onAdd()
    }

    fun onPreview(view: View){
        postMediaEventListener?.onPreview()
    }
    fun onDelete(view: View){
        postMediaEventListener?.onDelete()
    }
    fun onCheckedChange(isFeatured: Boolean){
        postMediaEventListener?.onCheckedChange(isFeatured)
    }
}