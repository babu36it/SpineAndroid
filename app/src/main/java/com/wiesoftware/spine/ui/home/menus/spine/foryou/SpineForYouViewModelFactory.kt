package com.wiesoftware.spine.ui.home.menus.spine.foryou

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.wiesoftware.spine.data.repo.HomeRepository

/**
 * Created by Vivek kumar on 8/13/2020.
 * E-mail:- vivekpcst.kumar@gmail.com
 */
@Suppress("UNCHECKED_CAST")
class SpineForYouViewModelFactory(
    private val homeRepositry: HomeRepository
) : ViewModelProvider.NewInstanceFactory(){

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
         return SpineForYouViewModel(homeRepositry) as T
    }
}