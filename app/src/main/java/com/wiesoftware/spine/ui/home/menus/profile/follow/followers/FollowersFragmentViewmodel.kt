package com.wiesoftware.spine.ui.home.menus.profile.follow.followers

import androidx.lifecycle.ViewModel
import com.wiesoftware.spine.data.repo.HomeRepository

/**
 * Created by Vivek kumar on 1/20/2021.
 * Email: vivekpcst.kumar@gmail.com.
 */
class FollowersFragmentViewmodel(val homeRepositry: HomeRepository): ViewModel() {

    fun getLoggedInUser()=homeRepositry.getUser()

}