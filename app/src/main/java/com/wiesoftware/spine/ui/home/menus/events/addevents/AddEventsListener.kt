package com.wiesoftware.spine.ui.home.menus.events.addevents

/**
 * Created by Vivek kumar on 11/13/2020.
 * E-mail:- vivekpcst.kumar@gmail.com
 */
interface AddEventsListener {
    fun onBack()
    fun onPost(
        title: String,
        timeZone: String,
        location: String,
        link: String,
        fee: String,
        attendees: String,
        book_event_url:String
    )
    fun onDelete()
    fun onPreview(
        title: String,
        description: String,
        location: String,
        link: String,
        fee: String,
        attendees: String,
        book_event_url:String
    )
    fun onAdPic()
    fun selectEventType()
    fun onAllowComments(isChecked: Boolean)
    fun onAllowParticipants(isChecked: Boolean)
    fun onStartDate()
    fun onEndDate()
    fun onStartTime()
    fun onEndTime()
    fun onCurency()
    fun onAddAditionalCategory()
    fun onAddNewCategory(category: String)
    fun onAllowPaid(isChecked: Boolean)
    fun goToHome()
    fun addNewEvent()
}