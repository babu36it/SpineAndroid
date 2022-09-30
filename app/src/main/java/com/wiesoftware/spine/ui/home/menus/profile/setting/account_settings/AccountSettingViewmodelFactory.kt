package com.wiesoftware.spine.ui.home.menus.profile.setting.account_settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.wiesoftware.spine.data.repo.HomeRepositry

/**
 * Created by Vivek kumar on 1/21/2021.
 * Email: vivekpcst.kumar@gmail.com.
 */
@Suppress("UNCHECKED_CAST")
class AccountSettingViewmodelFactory(val homeRepositry: HomeRepositry):ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return AccountSettingViewmodel(homeRepositry) as T
    }
}