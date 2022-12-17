package com.wiesoftware.spine.ui.home.menus.profile.chat

import android.view.View
import androidx.lifecycle.ViewModel
import com.wiesoftware.spine.data.repo.HomeRepository

/**
 * Created by Vivek kumar on 1/19/2021.
 * E-mail:- vivekpcst.kumar@gmail.com
 */
class ChatActivityViewmodel(val homeRepositry: HomeRepository): ViewModel() {

    var chatActivityEventListener: ChatActivityEventListener?=null
    var msg: String?= null

    fun getLoggedInUser() = homeRepositry.getUser()

    fun onBack(view: View){
        chatActivityEventListener?.onBack()
    }
    fun onSendMessage(view: View){
        if (msg.isNullOrEmpty()){
            return
        }
        chatActivityEventListener?.onMsgSend(msg)
    }
}