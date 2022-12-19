package com.wiesoftware.spine.ui.home.menus.events

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.wiesoftware.spine.data.repo.EventRepository

/**
 * Created by Vivek kumar on 11/12/2020.
 * E-mail:- vivekpcst.kumar@gmail.com
 */
@Suppress("UNCHECKED_CAST")
class EventFragmentViewModelFactory(val eventRepositry: EventRepository):ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return EventFragmentViewModel(eventRepositry) as T
    }
}