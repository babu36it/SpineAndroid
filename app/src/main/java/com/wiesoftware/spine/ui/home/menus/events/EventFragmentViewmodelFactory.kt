package com.wiesoftware.spine.ui.home.menus.events

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.wiesoftware.spine.data.repo.EventRepositry

/**
 * Created by Vivek kumar on 11/12/2020.
 * E-mail:- vivekpcst.kumar@gmail.com
 */
@Suppress("UNCHECKED_CAST")
class EventFragmentViewmodelFactory(val eventRepositry: EventRepositry):ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return EventFragmentViewModel(eventRepositry) as T
    }
}