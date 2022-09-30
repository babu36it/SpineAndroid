package com.wiesoftware.spine.ui.home.menus.profile.tabs.events

import android.view.View
import androidx.lifecycle.ViewModel
import com.wiesoftware.spine.data.repo.HomeRepositry

/**
 * Created by Vivek kumar on 12/4/2020.
 * E-mail:- vivekpcst.kumar@gmail.com
 */
class EventsViewmodel(val homeRepositry: HomeRepositry): ViewModel() {

    var eventsEventListener: EventsEventListener?=null

    fun getLoggedInUser() =  homeRepositry.getUser()

    fun onAddevent(view: View){
        eventsEventListener?.onAddevent()
    }
}