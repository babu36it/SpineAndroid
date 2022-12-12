package com.wiesoftware.spine.ui.home.menus.events.eventcomment.eventreply

import android.view.View
import androidx.lifecycle.ViewModel
import com.wiesoftware.spine.data.repo.HomeRepository

/**
 * Created by Vivek kumar on 3/2/2021.
 * Email: vivekpcst.kumar@gmail.com.
 */
class EventReplyViewmodel(val homeRepositry: HomeRepository): ViewModel() {

    var reply=""

    var eventReplyEventListener:  EventReplyEventListener?=null
    fun getLoggedInUser()=homeRepositry.getUser()

    fun onBack(view: View){
        eventReplyEventListener?.onBack()
    }

    fun onEventReply(view: View){
        if (reply.isEmpty()){
            return
        }
        eventReplyEventListener?.onEventReply(reply)
    }
}