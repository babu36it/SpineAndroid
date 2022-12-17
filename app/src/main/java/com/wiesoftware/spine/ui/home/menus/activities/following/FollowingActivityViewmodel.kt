package com.wiesoftware.spine.ui.home.menus.activities.following

import androidx.lifecycle.ViewModel
import com.wiesoftware.spine.data.repo.HomeRepository

/**
 * Created by Vivek kumar on 1/4/2021.
 * E-mail:- vivekpcst.kumar@gmail.com
 */
class FollowingActivityViewmodel(val homeRepositry: HomeRepository): ViewModel() {

    fun getLoggedInUser()=homeRepositry.getUser()
}