package com.wiesoftware.spine.ui.home.menus.profile.masseges.msg

import androidx.lifecycle.ViewModel
import com.wiesoftware.spine.data.repo.HomeRepositry

/**
 * Created by Vivek kumar on 1/18/2021.
 * E-mail:- vivekpcst.kumar@gmail.com
 */
class MsgFragmentViewmodel(val homeRepositry: HomeRepositry): ViewModel() {

    fun getLoggedInUser() = homeRepositry.getUser()
}