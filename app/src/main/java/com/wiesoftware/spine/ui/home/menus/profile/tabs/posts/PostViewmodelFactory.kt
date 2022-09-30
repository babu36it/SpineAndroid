package com.wiesoftware.spine.ui.home.menus.profile.tabs.posts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.wiesoftware.spine.data.repo.HomeRepositry

/**
 * Created by Vivek kumar on 12/14/2020.
 * E-mail:- vivekpcst.kumar@gmail.com
 */
@Suppress("UNCHECKED_CAST")
class PostViewmodelFactory(val homeRepositry: HomeRepositry): ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return PostViewmodel(homeRepositry) as T
    }
}