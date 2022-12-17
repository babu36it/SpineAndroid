package com.wiesoftware.spine.ui.home.menus.events.calendarevents

import android.view.View
import androidx.lifecycle.ViewModel

/**
 * Created by Vivek kumar on 3/15/2021.
 * Email: vivekpcst.kumar@gmail.com.
 */
class CaklendarEventViewmodel(): ViewModel() {
    var calendarEventsEventListener: CalendarEventsEventListener?=null

    fun onBack(view: View){
        calendarEventsEventListener?.onBack()
    }
    fun onSaveEvent(view: View){
        calendarEventsEventListener?.onSaveEvents()
    }

}