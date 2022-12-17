package com.wiesoftware.spine.ui.home.menus.events.event_details

/**
 * Created by Vivek kumar on 1/11/2021.
 * E-mail:- vivekpcst.kumar@gmail.com
 */
interface EventDetailEventListener {
    fun onBack()
    fun openMessageDialog()
    fun onShareEvent()
    fun onEventSaved()
    fun onRequestToAttend()
    fun postEventComment()
    fun onShowPopupMenu()
    fun editRegistration()
    fun onProfileView()
    fun onTargetFocusChanged(hasFocus: Boolean)
    fun onHostedIn()
    fun onLinkClicked()
    fun onCalenderEvent()
    fun eventClick()
}