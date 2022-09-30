package com.wiesoftware.spine.ui.home.menus.events.eventcomment

import android.view.View
import androidx.lifecycle.ViewModel
import com.wiesoftware.spine.data.repo.HomeRepositry

/**
 * Created by Vivek kumar on 3/2/2021.
 * Email: vivekpcst.kumar@gmail.com.
 */
class EventCommentViewmodel(val homeRepositry: HomeRepositry): ViewModel() {

    var commentMsg=""

    var eventCommentEventListener: EventCommentEventListener? = null

    fun getLoggedInUser()=homeRepositry.getUser()

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