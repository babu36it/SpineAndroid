package com.wiesoftware.spine.ui.home.menus.activities.you

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.wiesoftware.spine.data.repo.HomeRepositry

/**
 * Created by Vivek kumar on 11/27/2020.
 * E-mail:- vivekpcst.kumar@gmail.com
 */
@Suppress("UNCHECKED_CAST")
class YouActivityViewmodelFactory(
    val homeRepositry: HomeRepositry
): ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return YouActivityViewmodel(homeRepositry) as T
    }
}