package com.wiesoftware.spine.ui.home.menus.spine.addposts.hashtags.autosearchfrag

import android.view.View
import androidx.lifecycle.ViewModel

/**
 * Created by Vivek kumar on 3/16/2021.
 * Email: vivekpcst.kumar@gmail.com.
 */
class AutoSearchViewModel():ViewModel() {

    var autoSearchEventListener: AutoSearchEventListener? = null

    fun onCloseautosearch(view: View){
        autoSearchEventListener?.onCloseAutosearch()
    }
}