package com.wiesoftware.spine.ui.home.menus.spine.addposts.previewpost

import android.view.View
import androidx.lifecycle.ViewModel

/**
 * Created by Vivek kumar on 3/22/2021.
 * Email: vivekpcst.kumar@gmail.com.
 */
class PreviewPostViewModel():ViewModel() {

    var previewPostEventListener: PreviewPostEventListener? = null

    fun onBack(view: View){
        previewPostEventListener?.onBack()
    }
    fun onPost(view: View){
        previewPostEventListener?.onPost()
    }
}