package com.wiesoftware.spine.ui.home.menus.profile.tabs.podcasts

import android.view.View
import androidx.lifecycle.ViewModel

/**
 * Created by Vivek kumar on 12/4/2020.
 * E-mail:- vivekpcst.kumar@gmail.com
 */
class PodcastsViewModel(): ViewModel() {

    var podcastsEventListner: PodcastsEventListner? = null

    fun onAddPodcast(view: View){
        podcastsEventListner?.onAddPodCast()
    }
}