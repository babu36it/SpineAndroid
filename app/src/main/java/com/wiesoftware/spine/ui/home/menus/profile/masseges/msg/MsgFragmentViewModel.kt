package com.wiesoftware.spine.ui.home.menus.profile.masseges.msg

import androidx.lifecycle.ViewModel
import com.wiesoftware.spine.data.repo.HomeRepository

/**
 * Created by Vivek kumar on 1/18/2021.
 * E-mail:- vivekpcst.kumar@gmail.com
 */
class MsgFragmentViewModel(val homeRepositry: HomeRepository): ViewModel() {

    fun getLoggedInUser() = homeRepositry.getUser()
}