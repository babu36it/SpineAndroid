package com.wiesoftware.spine.ui.home.menus.spine.welcome

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.wiesoftware.spine.data.repo.HomeRepositry

/**
 * Created by Vivek kumar on 10/22/2020.
 * E-mail:- vivekpcst.kumar@gmail.com
 */
@Suppress("UNCHECKED_CAST")
class ViewWelcomeViewmodelFactory(private val homeRepositry: HomeRepositry):
ViewModelProvider.NewInstanceFactory(){
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ViewWelcomeViewmodel(homeRepositry) as T
    }
}