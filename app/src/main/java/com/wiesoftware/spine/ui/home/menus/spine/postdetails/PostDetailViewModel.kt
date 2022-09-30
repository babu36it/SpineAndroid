package com.wiesoftware.spine.ui.home.menus.spine.postdetails

import android.view.View
import androidx.lifecycle.ViewModel

/**
 * Created by Vivek kumar on 3/3/2021.
 * Email: vivekpcst.kumar@gmail.com.
 */
class PostDetailViewModel(): ViewModel() {

    var postDetailEventListener: PostDetailEventListener?= null
    fun onBack(view: View){
        postDetailEventListener?.onBack()
    }
}