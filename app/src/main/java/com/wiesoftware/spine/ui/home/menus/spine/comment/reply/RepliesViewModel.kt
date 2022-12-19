package com.wiesoftware.spine.ui.home.menus.spine.comment.reply

import android.view.View
import androidx.lifecycle.ViewModel

/**
 * Created by Vivek kumar on 3/1/2021.
 * Email: vivekpcst.kumar@gmail.com.
 */
class RepliesViewModel(): ViewModel() {
    var repliesEventListener: RepliesEventListener?=null
    var reply: String?= null

    fun onBack(view: View){
        repliesEventListener?.onBack()
    }
    fun onSend(view: View){
        if (reply!=null){
            repliesEventListener?.onReply(reply!!)
        }
    }
}