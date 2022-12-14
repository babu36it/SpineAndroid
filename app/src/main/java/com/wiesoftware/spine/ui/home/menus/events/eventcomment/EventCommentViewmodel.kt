package com.wiesoftware.spine.ui.home.menus.events.eventcomment

import android.view.View
import androidx.lifecycle.ViewModel
import com.wiesoftware.spine.data.repo.EventRepositry

/**
 * Created by Vivek kumar on 3/2/2021.
 * Email: vivekpcst.kumar@gmail.com.
 */
class EventCommentViewmodel(val eventRepositry: EventRepositry): ViewModel() {

    var commentMsg=""

    var eventCommentEventListener: EventCommentEventListener? = null

    fun getLoggedInUser()=eventRepositry.getUser()

    fun onBack(view: View){
        eventCommentEventListener?.onBack()
    }
    fun onSendComment(view: View){
        if (commentMsg.isEmpty()){
            return
        }
        eventCommentEventListener?.onSendComment(commentMsg)
    }
}