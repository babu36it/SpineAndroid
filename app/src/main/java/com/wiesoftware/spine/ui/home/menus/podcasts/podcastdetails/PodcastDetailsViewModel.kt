package com.wiesoftware.spine.ui.home.menus.podcasts.podcastdetails

import android.view.View
import androidx.lifecycle.ViewModel
import com.wiesoftware.spine.data.repo.HomeRepository

/**
 * Created by Vivek kumar on 2/18/2021.
 * Email: vivekpcst.kumar@gmail.com.
 */
class PodcastDetailsViewModel(val homeRepositry: HomeRepository): ViewModel() {
    var podcastDetailsEventListener: PodcastDetailsEventListener? =null

    fun  getLoggedInUser()=homeRepositry.getUser()
    fun onBack(view: View){
        podcastDetailsEventListener?.onBack()
    }
    fun onLike(view: View){
        podcastDetailsEventListener?.onLike()
    }
    fun onMore(view: View){
        podcastDetailsEventListener?.onMore()
    }
    fun onShare(view: View){
        podcastDetailsEventListener?.onShare()
    }

}