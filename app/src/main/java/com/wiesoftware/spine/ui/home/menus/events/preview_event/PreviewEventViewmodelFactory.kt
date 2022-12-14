package com.wiesoftware.spine.ui.home.menus.events.preview_event

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.wiesoftware.spine.data.repo.EventRepositry

/**
 * Created by Vivek kumar on 1/15/2021.
 * E-mail:- vivekpcst.kumar@gmail.com
 */
@Suppress("UNCHECKED_CAST")
class PreviewEventViewmodelFactory(val eventRepositry: EventRepositry):ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return PreviewEventViewmodel(eventRepositry) as T
    }
}