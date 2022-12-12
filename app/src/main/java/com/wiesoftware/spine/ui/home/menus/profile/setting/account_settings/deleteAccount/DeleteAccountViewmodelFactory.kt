package com.wiesoftware.spine.ui.home.menus.profile.setting.account_settings.deleteAccount

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.wiesoftware.spine.data.repo.HomeRepository

/**
 * Created by Vivek kumar on 3/4/2021.
 * Email: vivekpcst.kumar@gmail.com.
 */
@Suppress("UNCHECKED_CAST")
class DeleteAccountViewmodelFactory (val homeRepositry: HomeRepository): ViewModelProvider.NewInstanceFactory(){

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return DeleteAccountViewmodel(homeRepositry) as T
    }
}