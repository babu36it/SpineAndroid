package com.wiesoftware.spine.ui.home.menus.events.select_users

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.wiesoftware.spine.data.repo.EventRepositry

/**
 * Created by Vivek kumar on 1/12/2021.
 * E-mail:- vivekpcst.kumar@gmail.com
 */
@Suppress("UNCHECKED_CAST")
class SelectUsersViewmodelFactory(val eventRepositry: EventRepositry):ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return SelectUsersViewmodel(eventRepositry) as T
    }
}