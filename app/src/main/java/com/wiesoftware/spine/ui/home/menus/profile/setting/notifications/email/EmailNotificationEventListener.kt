package com.wiesoftware.spine.ui.home.menus.profile.setting.notifications.email

/**
 * Created by Vivek kumar on 2/23/2021.
 * Email: vivekpcst.kumar@gmail.com.
 */
interface EmailNotificationEventListener {
    fun onEmailUpdateChanged(isChecked: Boolean)
    fun onICalChanged(checked:Boolean)
    fun onHQSurveyChanged(checked: Boolean)
    fun onSpineHQChanged(checked: Boolean)
    fun onEveAndPodChanged(checked: Boolean)
    fun onRepliesChanged(checked: Boolean)
    fun onMessageChanged(checked: Boolean)
}