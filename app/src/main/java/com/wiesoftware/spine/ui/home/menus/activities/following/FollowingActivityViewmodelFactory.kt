package com.wiesoftware.spine.ui.home.menus.activities.following

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.wiesoftware.spine.data.repo.HomeRepositry

/**
 * Created by Vivek kumar on 1/4/2021.
 * E-mail:- vivekpcst.kumar@gmail.com
 */
@Suppress("UNCHECKED_CAST")
class FollowingActivityViewmodelFactory(val homeRepositry: HomeRepositry):ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return FollowingActivityViewmodel(homeRepositry) as T
    }
}