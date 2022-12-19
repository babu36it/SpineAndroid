package com.wiesoftware.spine.ui.home.menus.spine.comment.storycomment.reply

import android.view.View
import androidx.lifecycle.ViewModel

/**
 * Created by Vivek kumar on 3/9/2021.
 * Email: vivekpcst.kumar@gmail.com.
 */
class StoryCommentReplyViewModel(): ViewModel() {
    var reply = ""
    var storyCommentReplyEventListener: StoryCommentReplyEventListener?=null
    fun onBack(view: View){
        storyCommentReplyEventListener?.onBack()
    }
    fun onSend(view: View){
        if (reply.isEmpty()){
            return
        }
        storyCommentReplyEventListener?.onSend(reply)
    }
}