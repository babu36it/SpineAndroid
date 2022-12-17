package com.wiesoftware.spine.ui.home.menus.profile.setting.notifications.mobile

/**
 * Created by Vivek kumar on 2/23/2021.
 * Email: vivekpcst.kumar@gmail.com.
 */
interface MobileNotificationEventListener {
    fun onPushNotificationChanged(isChecked:Boolean)
    fun onEveryMemberNotificationChanged(isChecked: Boolean)
    fun onImpulseNotificationChanged(isChecked: Boolean)
    fun onActivityFollowNotificationChanged(isChecked: Boolean)
    fun onMessageNotificationChanged(isChecked: Boolean)
    fun onSavedEventNotificationChanged(isChecked: Boolean)
    fun onEventUpdateNotificationChanged(isChecked: Boolean)
    fun onCommentNotificationChanged(isChecked: Boolean)
    fun onLikeNotificationChanged(isChecked: Boolean)
}