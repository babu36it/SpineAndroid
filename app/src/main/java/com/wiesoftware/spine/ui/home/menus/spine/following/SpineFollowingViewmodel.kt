package com.wiesoftware.spine.ui.home.menus.spine.following

import androidx.lifecycle.ViewModel
import com.wiesoftware.spine.data.repo.HomeRepository

/**
 * Created by Vivek kumar on 12/14/2020.
 * E-mail:- vivekpcst.kumar@gmail.com
 */
class SpineFollowingViewmodel(
    val homeRepositry: HomeRepository)
    : ViewModel() {
        var spineFollowingEventListener: SpineFollowingEventListener?=null

    fun getLoggedInUser()=homeRepositry.getUser()
}