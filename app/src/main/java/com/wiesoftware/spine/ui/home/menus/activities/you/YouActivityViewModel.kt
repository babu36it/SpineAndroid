package com.wiesoftware.spine.ui.home.menus.activities.you

import androidx.lifecycle.ViewModel
import com.wiesoftware.spine.data.repo.HomeRepository

/**
 * Created by Vivek kumar on 11/27/2020.
 * E-mail:- vivekpcst.kumar@gmail.com
 */
class YouActivityViewModel(
    val homeRepositry: HomeRepository
):ViewModel() {

    fun getLoggedInUser()=homeRepositry.getUser()

}