package com.wiesoftware.spine.ui.home.menus.profile.setting.account_settings.messaging

import android.widget.RadioGroup

/**
 * Created by Vivek kumar on 1/28/2021.
 * Email: vivekpcst.kumar@gmail.com.
 */
interface MessagingEventListener {

    fun onBack()
    fun onMessagingTypeChanged(rg: RadioGroup, id:Int)
}