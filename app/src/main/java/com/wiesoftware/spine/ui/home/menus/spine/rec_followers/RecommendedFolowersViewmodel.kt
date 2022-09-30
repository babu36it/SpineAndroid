package com.wiesoftware.spine.ui.home.menus.spine.rec_followers

import android.view.View
import androidx.lifecycle.ViewModel
import com.wiesoftware.spine.data.repo.HomeRepositry

/**
 * Created by Vivek kumar on 12/17/2020.
 * E-mail:- vivekpcst.kumar@gmail.com
 */
class RecommendedFolowersViewmodel(val homeRepositry: HomeRepositry):ViewModel() {

    var recommendedFollowersEventListener: RecommendedFollowersEventListener?=null

    fun getLoggedInUser()=homeRepositry.getUser()

    fun onBack(view:View){
        recommendedFollowersEventListener?.onBack()
    }
}