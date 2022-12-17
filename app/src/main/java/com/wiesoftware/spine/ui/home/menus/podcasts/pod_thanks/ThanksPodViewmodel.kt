package com.wiesoftware.spine.ui.home.menus.podcasts.pod_thanks

import android.view.View
import androidx.lifecycle.ViewModel

/**
 * Created by Vivek kumar on 5/6/2021.
 * E-mail:- vivekpcst.kumar@gmail.com
 */
class ThanksPodViewmodel():ViewModel() {

    var thanksPodEventListener: ThanksPodEventListener? = null

    fun onClose(view: View){
        thanksPodEventListener?.onClose()
    }
    fun closePodcast(view: View){
        thanksPodEventListener?.onClosePodcast()
    }
}