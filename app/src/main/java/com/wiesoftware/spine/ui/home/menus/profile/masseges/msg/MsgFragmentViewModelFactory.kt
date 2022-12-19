package com.wiesoftware.spine.ui.home.menus.profile.masseges.msg

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.wiesoftware.spine.data.repo.HomeRepository

/**
 * Created by Vivek kumar on 1/18/2021.
 * E-mail:- vivekpcst.kumar@gmail.com
 */
@Suppress("UNCHECKED_CAST")
class MsgFragmentViewModelFactory(val homeRepositry: HomeRepository): ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return MsgFragmentViewModel(homeRepositry) as T
    }
}