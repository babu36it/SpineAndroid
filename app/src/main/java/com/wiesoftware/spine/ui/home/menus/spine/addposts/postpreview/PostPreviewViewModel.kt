package com.wiesoftware.spine.ui.home.menus.spine.addposts.postpreview

import android.view.View
import androidx.lifecycle.ViewModel

/**
 * Created by Vivek kumar on 3/16/2021.
 * Email: vivekpcst.kumar@gmail.com.
 */
class PostPreviewViewModel(): ViewModel() {
    var postPreviewEventListener: PostPreviewEventListener? = null
    fun onBack(view: View){
        postPreviewEventListener?.onBack()
    }
    fun onPost(view: View){
        postPreviewEventListener?.onPost()
    }
}