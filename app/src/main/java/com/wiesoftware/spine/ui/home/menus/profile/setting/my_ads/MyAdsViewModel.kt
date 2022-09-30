package com.wiesoftware.spine.ui.home.menus.profile.setting.my_ads

import android.view.View
import androidx.lifecycle.ViewModel
import com.wiesoftware.spine.ui.home.menus.profile.myprofile.MyProfileEventListener

/**
 * Created by Vivek kumar on 5/5/2021.
 * E-mail:- vivekpcst.kumar@gmail.com
 */
class MyAdsViewModel():ViewModel() {
    var myAdsEventListener: MyAdsEventListener? = null

    fun onBack(view: View){
        myAdsEventListener?.onBack()
    }

}