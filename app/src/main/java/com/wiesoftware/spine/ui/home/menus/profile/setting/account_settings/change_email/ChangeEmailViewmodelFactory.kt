package com.wiesoftware.spine.ui.home.menus.profile.setting.account_settings.change_email

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.wiesoftware.spine.data.repo.HomeRepositry

/**
 * Created by Vivek kumar on 1/27/2021.
 * Email: vivekpcst.kumar@gmail.com.
 */
@Suppress("UNCHECKED_CAST")
class ChangeEmailViewmodelFactory(val homeRepositry: HomeRepositry): ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ChangeEmailViewmodel(homeRepositry) as T
    }
}