package com.wiesoftware.spine.ui.home.menus.profile.setting.account_settings.messaging

import android.view.View
import android.widget.RadioGroup
import androidx.lifecycle.ViewModel
import com.wiesoftware.spine.data.repo.HomeRepositry

/**
 * Created by Vivek kumar on 1/28/2021.
 * Email: vivekpcst.kumar@gmail.com.
 */
class MessagingViewmodel(val homeRepositry: HomeRepositry): ViewModel() {

    var messagingEventListener: MessagingEventListener?= null

    fun getLoggedInUser()=homeRepositry.getUser()

    fun onBack(view: View){
        messagingEventListener?.onBack()
    }
    fun onMessagingTypeChanged(rg: RadioGroup,id:Int){
        messagingEventListener?.onMessagingTypeChanged(rg, id)
    }

}