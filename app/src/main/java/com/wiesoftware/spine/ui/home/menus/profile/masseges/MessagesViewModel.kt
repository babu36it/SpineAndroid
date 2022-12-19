package com.wiesoftware.spine.ui.home.menus.profile.masseges

import android.view.View
import androidx.lifecycle.ViewModel

/**
 * Created by Vivek kumar on 1/18/2021.
 * E-mail:- vivekpcst.kumar@gmail.com
 */
class MessagesViewModel(): ViewModel() {
    var messagesEventListener: MessagesEventListener?=null

    fun onBack(view: View){
        messagesEventListener?.onBack()
    }
}