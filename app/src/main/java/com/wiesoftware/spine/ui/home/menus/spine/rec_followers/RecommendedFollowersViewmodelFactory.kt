package com.wiesoftware.spine.ui.home.menus.spine.rec_followers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.wiesoftware.spine.data.repo.HomeRepositry

/**
 * Created by Vivek kumar on 12/17/2020.
 * E-mail:- vivekpcst.kumar@gmail.com
 */
@Suppress("UNCHECKED_CAST")
class RecommendedFollowersViewmodelFactory(val homeRepositry: HomeRepositry):ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return RecommendedFolowersViewmodel(homeRepositry) as T
    }
}