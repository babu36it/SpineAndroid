package com.wiesoftware.spine.ui.home.menus.activities.you

import androidx.lifecycle.ViewModel
import com.wiesoftware.spine.data.repo.HomeRepositry

/**
 * Created by Vivek kumar on 11/27/2020.
 * E-mail:- vivekpcst.kumar@gmail.com
 */
class YouActivityViewmodel(
    val homeRepositry: HomeRepositry
):ViewModel() {

    fun getLoggedInUser()=homeRepositry.getUser()

}