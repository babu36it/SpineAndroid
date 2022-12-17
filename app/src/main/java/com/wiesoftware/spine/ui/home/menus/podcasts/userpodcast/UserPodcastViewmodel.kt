package com.wiesoftware.spine.ui.home.menus.podcasts.userpodcast

import android.view.View
import androidx.lifecycle.ViewModel
import com.wiesoftware.spine.data.repo.HomeRepository

/**
 * Created by Vivek kumar on 2/17/2021.
 * Email: vivekpcst.kumar@gmail.com.
 */
class UserPodcastViewmodel(val homeRepositry: HomeRepository): ViewModel() {


    var userPodcastEventListener: UserPodcastEventListener?=null
    fun getLoggedInUser()=homeRepositry.getUser()
    fun onBack(view: View){
        userPodcastEventListener?.onBack()
    }
}