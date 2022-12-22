package com.wiesoftware.spine.ui.home.menus.podcasts

import android.view.View
import androidx.lifecycle.ViewModel

/**
 * Created by Vivek kumar on 9/10/2020.
 * E-mail:- vivekpcst.kumar@gmail.com
 */
class PodcastsFragmentViewModel: ViewModel() {

    var podcastFragmentEventListener: PodcastFragmentEventListener?=null

    fun onAddPodcast(view: View){
        podcastFragmentEventListener?.onAddPodcast()
    }
}