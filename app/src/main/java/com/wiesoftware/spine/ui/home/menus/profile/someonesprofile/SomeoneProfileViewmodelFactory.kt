package com.wiesoftware.spine.ui.home.menus.profile.someonesprofile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.wiesoftware.spine.data.repo.HomeRepositry

/**
 * Created by Vivek kumar on 2/23/2021.
 * Email: vivekpcst.kumar@gmail.com.
 */
@Suppress("UNCHECKED_CAST")
class SomeoneProfileViewmodelFactory(val homeRepositry: HomeRepositry): ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return SomeoneProfileViewmodel(homeRepositry) as T
    }
}