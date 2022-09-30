package com.wiesoftware.spine.ui.home.menus.spine.categories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.wiesoftware.spine.data.repo.HomeRepositry

/**
 * Created by Vivek kumar on 12/16/2020.
 * E-mail:- vivekpcst.kumar@gmail.com
 */
@Suppress("UNCHECKED_CAST")
class TrendingCatViewmodelFactory(val homeRepositry: HomeRepositry):ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return TrendingCatViewmodel(homeRepositry) as T
    }
}