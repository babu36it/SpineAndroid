package com.wiesoftware.spine.ui.home.menus.spine.practicioners

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.wiesoftware.spine.data.repo.HomeRepositry
import com.wiesoftware.spine.ui.home.menus.events.filter.FilterEventViewmodel

@Suppress("UNCHECKED_CAST")
class PracticionerModelFactory(val homeRepositry: HomeRepositry):  ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return SearchPracticionerViewmodel(homeRepositry) as T
    }
}