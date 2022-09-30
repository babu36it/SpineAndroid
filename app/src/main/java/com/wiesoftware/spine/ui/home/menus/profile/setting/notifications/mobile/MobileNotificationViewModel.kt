package com.wiesoftware.spine.ui.home.menus.profile.setting.notifications.mobile

import androidx.lifecycle.ViewModel
import com.wiesoftware.spine.data.repo.HomeRepositry

/**
 * Created by Vivek kumar on 2/23/2021.
 * Email: vivekpcst.kumar@gmail.com.
 */
class MobileNotificationViewModel(val homeRepositry: HomeRepositry): ViewModel() {
    var mobileNotificationEventListener: MobileNotificationEventListener? =null

    fun getLoggedInUser()=homeRepositry.getUser()

    fun onPushNotificationChanged(isChecked:Boolean){
        mobileNotificationEventListener?.onPushNotificationChanged(isChecked)
    }
    fun onEveryMemberNotificationChanged(isChecked: Boolean){
        mobileNotificationEventListener?.onEveryMemberNotificationChanged(isChecked)
    }
    fun onImpulseNotificationChanged(isChecked: Boolean){
        mobileNotificationEventListener?.onImpulseNotificationChanged(isChecked)
    }
    fun onActivityFollowNotificationChanged(isChecked: Boolean){
        mobileNotificationEventListener?.onActivityFollowNotificationChanged(isChecked)
    }
    fun onMessageNotificationChanged(isChecked: Boolean){
        mobileNotificationEventListener?.onMessageNotificationChanged(isChecked)
    }
    fun onSavedEventNotificationChanged(isChecked: Boolean){
        mobileNotificationEventListener?.onSavedEventNotificationChanged(isChecked)
    }
    fun onEventUpdateNotificationChanged(isChecked: Boolean){
        mobileNotificationEventListener?.onEventUpdateNotificationChanged(isChecked)
    }
    fun onCommentNotificationChanged(isChecked: Boolean){
        mobileNotificationEventListener?.onCommentNotificationChanged(isChecked)
    }
    fun onLikeNotificationChanged(isChecked: Boolean){
        mobileNotificationEventListener?.onLikeNotificationChanged(isChecked)
    }
}