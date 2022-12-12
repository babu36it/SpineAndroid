package com.wiesoftware.spine.ui.home.menus.events.select_users

import android.view.View
import androidx.lifecycle.ViewModel
import com.wiesoftware.spine.data.repo.HomeRepository

/**
 * Created by Vivek kumar on 1/12/2021.
 * E-mail:- vivekpcst.kumar@gmail.com
 */
class SelectUsersViewmodel(val homeRepository:HomeRepository): ViewModel() {

    var selectUsersEventListener: SelectUsersEventListener?=null

    fun getloggedInUser()=homeRepository.getUser()

    fun onBack(view: View){
        selectUsersEventListener?.onBack()
    }
    fun onNext(view: View){
        selectUsersEventListener?.onNext()
    }
}