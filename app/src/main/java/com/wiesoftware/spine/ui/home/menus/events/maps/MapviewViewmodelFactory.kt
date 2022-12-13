package com.wiesoftware.spine.ui.home.menus.events.maps

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.wiesoftware.spine.data.repo.EventRepositry

/**
 * Created by Vivek kumar on 1/22/2021.
 * Email: vivekpcst.kumar@gmail.com.
 */
@Suppress("UNCHECKED_CAST")
class MapviewViewmodelFactory(val eventRepositry: EventRepositry):ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return MapviewViewmodel(eventRepositry) as T
    }
}