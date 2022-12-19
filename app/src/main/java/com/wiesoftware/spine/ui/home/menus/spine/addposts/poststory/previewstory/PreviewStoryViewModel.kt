package com.wiesoftware.spine.ui.home.menus.spine.addposts.poststory.previewstory

import android.view.View
import androidx.lifecycle.ViewModel

/**
 * Created by Vivek kumar on 3/9/2021.
 * Email: vivekpcst.kumar@gmail.com.
 */
class PreviewStoryViewModel(): ViewModel() {

    var  previewStoryEventListener: PreviewStoryEventListener? = null
    fun onBack(view: View){
        previewStoryEventListener?.onBack()
    }
    fun onPostStory(view: View){
        previewStoryEventListener?.onStoryPost()
    }

}