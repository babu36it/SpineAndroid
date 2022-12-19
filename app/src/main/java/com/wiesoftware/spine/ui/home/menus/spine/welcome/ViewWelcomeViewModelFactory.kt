package com.wiesoftware.spine.ui.home.menus.spine.welcome

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.wiesoftware.spine.data.repo.HomeRepository

/**
 * Created by Vivek kumar on 10/22/2020.
 * E-mail:- vivekpcst.kumar@gmail.com
 */
@Suppress("UNCHECKED_CAST")
class ViewWelcomeViewModelFactory(private val homeRepositry: HomeRepository):
ViewModelProvider.NewInstanceFactory(){
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ViewWelcomeViewModel(homeRepositry) as T
    }
}