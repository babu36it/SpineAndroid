package com.wiesoftware.spine.ui.home.menus.activities.you

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.wiesoftware.spine.data.repo.HomeRepository

/**
 * Created by Vivek kumar on 11/27/2020.
 * E-mail:- vivekpcst.kumar@gmail.com
 */
@Suppress("UNCHECKED_CAST")
class YouActivityViewModelFactory(
    val homeRepositry: HomeRepository
): ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return YouActivityViewModel(homeRepositry) as T
    }
}