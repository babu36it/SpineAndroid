package com.wiesoftware.spine.ui.home.menus.activities.following

import androidx.lifecycle.ViewModel
import com.wiesoftware.spine.data.repo.HomeRepositry

/**
 * Created by Vivek kumar on 1/4/2021.
 * E-mail:- vivekpcst.kumar@gmail.com
 */
class FollowingActivityViewmodel(val homeRepositry: HomeRepositry): ViewModel() {

    fun getLoggedInUser()=homeRepositry.getUser()
}