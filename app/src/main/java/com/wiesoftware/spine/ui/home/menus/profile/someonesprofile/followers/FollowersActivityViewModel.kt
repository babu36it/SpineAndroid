package com.wiesoftware.spine.ui.home.menus.profile.someonesprofile.followers

import android.view.View
import androidx.lifecycle.ViewModel
import com.wiesoftware.spine.data.repo.HomeRepository

/**
 * Created by Vivek kumar on 2/24/2021.
 * Email: vivekpcst.kumar@gmail.com.
 */
class FollowersActivityViewModel(val homeRepositry: HomeRepository): ViewModel() {

    var followersActivityEventListener: FollowersActivityEventListener? = null

    fun getLoggedInUser()=homeRepositry.getUser()
    fun onBack(view: View){
        followersActivityEventListener?.onBack()
    }

}