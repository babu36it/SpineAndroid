package com.wiesoftware.spine.ui.home.menus.profile.tabs.posts

import android.view.View
import androidx.lifecycle.ViewModel
import com.wiesoftware.spine.data.repo.HomeRepository

/**
 * Created by Vivek kumar on 12/4/2020.
 * E-mail:- vivekpcst.kumar@gmail.com
 */
class PostViewModel(val homeRepositry: HomeRepository): ViewModel() {

    var postEventListner: PostEventListner?=null

    fun getLoggedInUser() = homeRepositry.getUser()

    fun onPostCreate(view: View){
        postEventListner?.createPost()
    }

}