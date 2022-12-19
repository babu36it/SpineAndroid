package com.wiesoftware.spine.ui.home.menus.events.eventcomment.eventreply

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.wiesoftware.spine.data.repo.EventRepository

/**
 * Created by Vivek kumar on 3/2/2021.
 * Email: vivekpcst.kumar@gmail.com.
 */
@Suppress("UNCHECKED_CAST")
class EventReplyViewModelFactory(val eventRepositry: EventRepository): ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return EventReplyViewModel(eventRepositry) as T
    }
}