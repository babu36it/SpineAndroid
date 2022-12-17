package com.wiesoftware.spine.ui.home.menus.profile.follow

import android.view.View
import androidx.lifecycle.ViewModel
import com.wiesoftware.spine.data.repo.HomeRepository

/**
 * Created by Vivek kumar on 1/20/2021.
 * Email: vivekpcst.kumar@gmail.com.
 */
class FollowViewmodel(val homeRepositry: HomeRepository): ViewModel() {


    fun getLoggedInUser()=homeRepositry.getUser()
    var followEventListener: FollowEventListener?=null
    fun onBack(view: View){
        followEventListener?.onBack()
    }
}