package com.wiesoftware.spine.ui.home.menus.events.addevents

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.wiesoftware.spine.data.repo.EventRepository

/**
 * Created by Vivek kumar on 11/13/2020.
 * E-mail:- vivekpcst.kumar@gmail.com
 */
@Suppress("UNCHECKED_CAST")
class AddEventsViewModelFactory(val eventRepositry: EventRepository): ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return AddEventViewModel(eventRepositry) as T
    }

}