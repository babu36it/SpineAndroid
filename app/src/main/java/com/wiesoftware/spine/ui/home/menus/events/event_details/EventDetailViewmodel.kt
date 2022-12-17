package com.wiesoftware.spine.ui.home.menus.events.event_details

import android.view.View
import androidx.lifecycle.ViewModel
import com.wiesoftware.spine.data.repo.EventRepository

/**
 * Created by Vivek kumar on 1/11/2021.
 * E-mail:- vivekpcst.kumar@gmail.com
 */
class EventDetailViewmodel(val eventRepositry: EventRepository): ViewModel() {

    var eventDetailEventListener: EventDetailEventListener?=null
    var eve_comment: String=""

    fun getLoggedInUser()=eventRepositry.getUser()

    fun onBack(view: View){
        eventDetailEventListener?.onBack()
    }
    fun openMessageDialog(view: View){
        eventDetailEventListener?.openMessageDialog()
    }
    fun onShareEvents(view: View){
        eventDetailEventListener?.onShareEvent()
    }
    fun onEventSaved(view: View){
        eventDetailEventListener?.onEventSaved()
    }
    fun onRequestToAttend(view: View){
        eventDetailEventListener?.onRequestToAttend()
    }
    fun postEventComment(view: View){

        eventDetailEventListener?.postEventComment()
    }
    fun onShowPopupMenu(view:View){
        eventDetailEventListener?.onShowPopupMenu()
    }
    fun editRegistration(view: View){
        eventDetailEventListener?.editRegistration()
    }
    fun onProfileView(view: View){
        eventDetailEventListener?.onProfileView()
    }
    fun onTargetFocusChanged(view: View, hasFocus: Boolean){
       eventDetailEventListener?.onTargetFocusChanged(hasFocus)
    }
    fun onHostedIn(view: View){
        eventDetailEventListener?.onHostedIn()
    }
    fun onLinkClicked(view: View){
        eventDetailEventListener?.onLinkClicked()
    }
    fun onCalenderEvent(view: View){
        eventDetailEventListener?.onCalenderEvent()
    }
    fun onEventClick(view: View){
        eventDetailEventListener?.eventClick()
    }
}