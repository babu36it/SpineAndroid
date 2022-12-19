package com.wiesoftware.spine.ui.home.menus.events.maps

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.wiesoftware.spine.data.repo.EventRepository

/**
 * Created by Vivek kumar on 1/22/2021.
 * Email: vivekpcst.kumar@gmail.com.
 */
@Suppress("UNCHECKED_CAST")
class MapviewViewModelFactory(val eventRepositry: EventRepository):ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return MapviewViewModel(eventRepositry) as T
    }
}