package com.wiesoftware.spine.ui.home.menus.podcasts.reviewpodcast

import android.view.View
import androidx.lifecycle.ViewModel

/**
 * Created by Vivek kumar on 4/9/2021.
 * Email: vivekpcst.kumar@gmail.com.
 */
class ReviewPodcastViewModel():ViewModel() {

    var reviewPodcastEventListener: ReviewPodcastEventListener? = null
    fun onBack(view: View){
        reviewPodcastEventListener?.onBack()
    }
    fun onSubmitPodcast(view: View){
        reviewPodcastEventListener?.onSubmitPodcast()
    }
}