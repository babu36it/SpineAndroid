package com.wiesoftware.spine.ui.home.menus.profile.follow.followers

import androidx.lifecycle.ViewModel
import com.wiesoftware.spine.data.repo.HomeRepositry

/**
 * Created by Vivek kumar on 1/20/2021.
 * Email: vivekpcst.kumar@gmail.com.
 */
class FollowersFragmentViewmodel(val homeRepositry: HomeRepositry): ViewModel() {

    fun getLoggedInUser()=homeRepositry.getUser()

}