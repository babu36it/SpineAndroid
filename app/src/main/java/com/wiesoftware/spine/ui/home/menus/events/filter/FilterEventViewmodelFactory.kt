package com.wiesoftware.spine.ui.home.menus.events.filter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.wiesoftware.spine.data.repo.EventRepositry
import com.wiesoftware.spine.data.repo.HomeRepositry

/**
 * Created by Vivek kumar on 12/16/2020.
 * E-mail:- vivekpcst.kumar@gmail.com
 */
@Suppress("UNCHECKED_CAST")
class FilterEventViewmodelFactory(val eventRepositry: EventRepositry): ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return FilterEventViewmodel(eventRepositry) as T
    }
}