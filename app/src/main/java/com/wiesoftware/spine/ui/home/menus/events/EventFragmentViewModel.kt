package com.wiesoftware.spine.ui.home.menus.events

import android.view.View
import androidx.lifecycle.ViewModel
import com.wiesoftware.spine.data.repo.HomeRepository

/**
 * Created by Vivek kumar on 9/9/2020.
 * E-mail:- vivekpcst.kumar@gmail.com
 */
class EventFragmentViewModel(val homeRepositry: HomeRepository) : ViewModel() {

    var eventFragmentEventListener: EventFragmentEventListener?=null

    fun getLoggedInUser() = homeRepositry.getUser()

    fun addEvent(view: View){
        eventFragmentEventListener?.onAddButtonClick()
    }
    fun filterEvent(view: View){
        eventFragmentEventListener?.onFilterEvent()
    }
    fun onMapview(view: View){
        eventFragmentEventListener?.onMapview()
    }
}