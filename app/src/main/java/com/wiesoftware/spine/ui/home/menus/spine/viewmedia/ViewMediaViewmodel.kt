package com.wiesoftware.spine.ui.home.menus.spine.viewmedia

import android.view.View
import androidx.lifecycle.ViewModel

/**
 * Created by Vivek kumar on 2/25/2021.
 * Email: vivekpcst.kumar@gmail.com.
 */
class ViewMediaViewmodel(): ViewModel() {
    var viewMediaEventListener: ViewMediaEventListener? =null

    fun onBack(view: View){
        viewMediaEventListener?.onBack()
    }
}