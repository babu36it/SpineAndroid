package com.wiesoftware.spine.ui.home.menus.podcasts.addrss

import android.view.View
import androidx.lifecycle.ViewModel

/**
 * Created by Vivek kumar on 4/6/2021.
 * Email: vivekpcst.kumar@gmail.com.
 */
class AddRssViewmodel(): ViewModel() {
    var rssLink: String? =null
    var addRssEventListener: AddRssEventListener?= null

    fun onBack(view: View){
        addRssEventListener?.onBack()
    }
    fun onValidateRss(view: View){
        if (rssLink.isNullOrEmpty()){
            return
        }
        addRssEventListener?.validateRss(rssLink!!)
    }
    fun onCloseInfo(view: View){
        addRssEventListener?.onCloseInfo()
    }
}