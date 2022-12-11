package com.wiesoftware.spine.ui.home.menus.events.eventcomment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.wiesoftware.spine.data.repo.EventRepositry
import com.wiesoftware.spine.data.repo.HomeRepositry

/**
 * Created by Vivek kumar on 3/2/2021.
 * Email: vivekpcst.kumar@gmail.com.
 */
@Suppress("UNCHECKED_CAST")
class EventCommentViewmodelFactory(val eventRepositry: EventRepositry): ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return EventCommentViewmodel(eventRepositry) as T
    }
}