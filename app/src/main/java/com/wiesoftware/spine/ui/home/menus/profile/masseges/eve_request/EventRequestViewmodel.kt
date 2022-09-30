package com.wiesoftware.spine.ui.home.menus.profile.masseges.eve_request

import androidx.lifecycle.ViewModel
import com.wiesoftware.spine.data.repo.HomeRepositry

/**
 * Created by Vivek kumar on 2/2/2021.
 * Email: vivekpcst.kumar@gmail.com.
 */
class EventRequestViewmodel(val homeRepositry: HomeRepositry): ViewModel() {

    var eventRequestEventListener: EventRequestEventListener?= null

    fun getLoggedInUser() = homeRepositry.getUser()
}