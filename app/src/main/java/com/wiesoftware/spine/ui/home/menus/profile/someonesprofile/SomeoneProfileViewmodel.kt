package com.wiesoftware.spine.ui.home.menus.profile.someonesprofile

import android.view.View
import androidx.lifecycle.ViewModel
import com.wiesoftware.spine.data.repo.HomeRepository

/**
 * Created by Vivek kumar on 2/23/2021.
 * Email: vivekpcst.kumar@gmail.com.
 */
class SomeoneProfileViewmodel(val homeRepositry: HomeRepository): ViewModel() {

    var someoneProfileEventListener: SomeoneProfileEventListener? = null

    fun getLoggedInUser()=homeRepositry.getUser()

    fun onBack(view: View){
        someoneProfileEventListener?.onBack()
    }
    fun onFollowers(view: View){
        someoneProfileEventListener?.onFollowers()
    }
    fun onFollowing(view: View){
        someoneProfileEventListener?.onFollowing()
    }
    fun onPost(view: View){
        someoneProfileEventListener?.onPost()
    }
    fun onEvents(view: View){
        someoneProfileEventListener?.onEvents()
    }
    fun onPods(view: View){
        someoneProfileEventListener?.onPods()
    }
    fun requestFollow(view: View){
        someoneProfileEventListener?.onRequestFollow()
    }
    fun sendMessage(view: View){
        someoneProfileEventListener?.sendMessage()
    }
    fun onCall(view: View){
        someoneProfileEventListener?.onCall()
    }
    fun onWebsite(view: View){
        someoneProfileEventListener?.onWebsite()
    }
    fun onAbout(view: View){
        someoneProfileEventListener?.onAbout()
    }
}