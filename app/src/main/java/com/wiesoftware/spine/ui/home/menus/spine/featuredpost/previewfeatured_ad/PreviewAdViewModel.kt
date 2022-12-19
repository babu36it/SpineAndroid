package com.wiesoftware.spine.ui.home.menus.spine.featuredpost.previewfeatured_ad

import android.view.View
import androidx.lifecycle.ViewModel

/**
 * Created by Vivek kumar on 5/14/2021.
 * E-mail:- vivekpcst.kumar@gmail.com
 */
class PreviewAdViewModel():ViewModel() {
    var previewAdEventListener: PreviewAdEventListener? = null

    fun onBack(view:View){
        previewAdEventListener?.onBack()
    }
    fun onNext(view: View){
        previewAdEventListener?.onNext()
    }
    fun onLinkClicked(view: View){
        previewAdEventListener?.onLinkClicked()
    }
}