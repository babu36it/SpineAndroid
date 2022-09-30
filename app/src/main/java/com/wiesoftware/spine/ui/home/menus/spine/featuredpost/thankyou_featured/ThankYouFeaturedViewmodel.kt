package com.wiesoftware.spine.ui.home.menus.spine.featuredpost.thankyou_featured

import android.view.View
import androidx.lifecycle.ViewModel

/**
 * Created by Vivek kumar on 5/14/2021.
 * E-mail:- vivekpcst.kumar@gmail.com
 */
class ThankYouFeaturedViewmodel(): ViewModel() {

    var thankYouFeaturedEventListener: ThankYouFeaturedEventListener? = null

    fun onClose(view: View){
        thankYouFeaturedEventListener?.onClose()
    }
}