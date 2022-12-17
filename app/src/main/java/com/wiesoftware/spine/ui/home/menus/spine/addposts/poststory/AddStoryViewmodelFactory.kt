package com.wiesoftware.spine.ui.home.menus.spine.addposts.poststory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.wiesoftware.spine.data.repo.HomeRepository

/**
 * Created by Vivek kumar on 11/11/2020.
 * E-mail:- vivekpcst.kumar@gmail.com
 */
@Suppress("UNCHECKED_CAST")
class AddStoryViewmodelFactory(val homeRepositry: HomeRepository): ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return AddStoryViewmodel(homeRepositry) as T
    }
}