package com.wiesoftware.spine.ui.home.menus.profile.myprofile

import android.view.View
import androidx.lifecycle.ViewModel
import com.wiesoftware.spine.data.repo.HomeRepositry
import kotlinx.android.synthetic.main.activity_my_profile.view.*

/**
 * Created by Vivek kumar on 1/20/2021.
 * Email: vivekpcst.kumar@gmail.com.
 */
class MyProfileViewmodel(val homeRepositry: HomeRepositry): ViewModel() {

    var myProfileEventListener: MyProfileEventListener?= null

    fun getLoggedInUser()=homeRepositry.getUser()

    fun onBack(view: View){
        myProfileEventListener?.onBack()
    }
    fun onMore(view: View){
        myProfileEventListener?.onMore()
    }
    fun onEditProfile(view: View){
        myProfileEventListener?.onEditProfile()
    }
    fun onPost(view: View){
        myProfileEventListener?.onPost()
    }
    fun onEvent(view: View){
        myProfileEventListener?.onEvent()
    }
    fun onPod(view: View){
        myProfileEventListener?.onPod()
    }
    fun onFollowers(view: View){
        myProfileEventListener?.onFollowers()
    }
    fun onFollowing(view: View){
        myProfileEventListener?.onFollowing()
    }
}