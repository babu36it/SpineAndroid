package com.wiesoftware.spine.ui.home.menus.podcasts.watch

import androidx.lifecycle.ViewModel
import com.wiesoftware.spine.data.repo.HomeRepository

/**
 * Created by Vivek kumar on 2/17/2021.
 * Email: vivekpcst.kumar@gmail.com.
 */
class WatchPodcastViewmodel(val homeRepositry: HomeRepository): ViewModel() {

    fun getLoggedInUser()=homeRepositry.getUser()
}