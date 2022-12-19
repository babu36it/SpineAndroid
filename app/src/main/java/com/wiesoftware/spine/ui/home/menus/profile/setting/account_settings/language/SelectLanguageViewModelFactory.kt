package com.wiesoftware.spine.ui.home.menus.profile.setting.account_settings.language

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.wiesoftware.spine.data.repo.HomeRepository

/**
 * Created by Vivek kumar on 2/5/2021.
 * Email: vivekpcst.kumar@gmail.com.
 */
@Suppress("UNCHECKED_CAST")
class SelectLanguageViewModelFactory(val  homeRepositry: HomeRepository): ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return SelectLanguageViewModel(homeRepositry) as T
    }
}