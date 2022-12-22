package com.wiesoftware.spine.ui.home.menus.spine.practicioners

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.wiesoftware.spine.data.repo.HomeRepository

@Suppress("UNCHECKED_CAST")
class PracticionerModelFactory(val homeRepositry: HomeRepository):  ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return SearchPracticionerViewmodel(homeRepositry) as T
    }
}