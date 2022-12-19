package com.wiesoftware.spine.ui.home.menus.profile.tabs.bookmark

import android.view.View
import androidx.lifecycle.ViewModel
import com.wiesoftware.spine.data.repo.HomeRepository

/**
 * Created by Vivek kumar on 12/4/2020.
 * E-mail:- vivekpcst.kumar@gmail.com
 */
class BookmarkViewModel(val homeRepositry: HomeRepository): ViewModel() {

    var bookmarkEventListener: BookmarkEventListener?= null

    fun getLoggedInUser()=homeRepositry.getUser()

    fun allPosts(view: View){
        bookmarkEventListener?.allEvents()
    }
    fun allEvents(view: View){
        bookmarkEventListener?.allEvents()
    }
    fun allStories(view: View){
        bookmarkEventListener?.allStories()
    }
    fun allPodcasts(view: View){
        bookmarkEventListener?.allPodcasts()
    }
}