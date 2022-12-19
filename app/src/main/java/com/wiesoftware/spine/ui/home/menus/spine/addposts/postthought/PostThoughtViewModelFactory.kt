package com.wiesoftware.spine.ui.home.menus.spine.addposts.postthought

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.wiesoftware.spine.data.repo.HomeRepository

/**
 * Created by Vivek kumar on 12/14/2020.
 * E-mail:- vivekpcst.kumar@gmail.com
 */
@Suppress("UNCHECKED_CAST")
class PostThoughtViewModelFactory(val homeRepositry: HomeRepository):ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return PostThoughtViewModel(homeRepositry) as T
    }
}