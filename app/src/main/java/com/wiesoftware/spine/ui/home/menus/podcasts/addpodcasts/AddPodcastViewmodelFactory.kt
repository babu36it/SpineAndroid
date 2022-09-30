package com.wiesoftware.spine.ui.home.menus.podcasts.addpodcasts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.wiesoftware.spine.data.repo.HomeRepositry

/**
 * Created by Vivek kumar on 2/1/2021.
 * Email: vivekpcst.kumar@gmail.com.
 */
@Suppress("UNCHECKED_CAST")
class AddPodcastViewmodelFactory(val homeRepositry: HomeRepositry): ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return AddPodcastViewmodel(homeRepositry) as T
    }
}