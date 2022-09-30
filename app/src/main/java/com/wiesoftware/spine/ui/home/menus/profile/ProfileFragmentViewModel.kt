package com.wiesoftware.spine.ui.home.menus.profile

import android.view.View
import androidx.lifecycle.ViewModel
import com.wiesoftware.spine.data.repo.HomeRepositry

/**
 * Created by Vivek kumar on 10/6/2020.
 * E-mail:- vivekpcst.kumar@gmail.com
 */
class ProfileFragmentViewModel(val homeRepositry: HomeRepositry): ViewModel() {

    var profileFragmentEventListener: ProfileFragmentEventListener?=null

    fun getLoggedInUser()=homeRepositry.getUser()

    fun onSettingButtonClick(view: View){
        profileFragmentEventListener?.onSettingBtnClick()
    }

    fun onMessageButtonClicked(view: View){
        profileFragmentEventListener?.onMessageBtnClick()
    }

    fun onViewProfileButtonClicked(view: View){
        profileFragmentEventListener?.onViewProfileClick()
    }

    fun onFollower(view: View){
        profileFragmentEventListener?.onFollowers()
    }
    fun onFollowing(view: View){
        profileFragmentEventListener?.onFollowing()
    }
    fun onAddPost(view: View){
        profileFragmentEventListener?.onAddPost()
    }
}