package com.wiesoftware.spine.ui.home.menus.events.event_details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.wiesoftware.spine.data.repo.EventRepository

/**
 * Created by Vivek kumar on 1/11/2021.
 * E-mail:- vivekpcst.kumar@gmail.com
 */
@Suppress("UNCHECKED_CAST")
class EventDetailViewModelFactory(val eventRepositry: EventRepository):ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return EventDetailViewModel(eventRepositry) as T
    }
}