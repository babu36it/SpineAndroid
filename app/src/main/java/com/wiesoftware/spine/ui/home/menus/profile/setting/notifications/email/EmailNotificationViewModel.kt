package com.wiesoftware.spine.ui.home.menus.profile.setting.notifications.email

import androidx.lifecycle.ViewModel
import com.wiesoftware.spine.data.repo.HomeRepository

/**
 * Created by Vivek kumar on 2/23/2021.
 * Email: vivekpcst.kumar@gmail.com.
 */
class EmailNotificationViewModel(val homeRepositry: HomeRepository): ViewModel() {

    var emailNotiFicationEventListener: EmailNotificationEventListener? = null
    fun onEmailUpdateChanged(isChecked: Boolean){
        emailNotiFicationEventListener?.onEmailUpdateChanged(isChecked)
    }
    fun onICalChanged(checked: Boolean){
        emailNotiFicationEventListener?.onICalChanged(checked)
    }
    fun onHQSurveyChanged(checked: Boolean){
        emailNotiFicationEventListener?.onHQSurveyChanged(checked)
    }
    fun onSpineHQChanged(checked: Boolean){
        emailNotiFicationEventListener?.onSpineHQChanged(checked)
    }
    fun onEveAndPodChanged(checked: Boolean){
        emailNotiFicationEventListener?.onEveAndPodChanged(checked)
    }
    fun onRepliesChanged(checked: Boolean){
        emailNotiFicationEventListener?.onRepliesChanged(checked)
    }
    fun onMessageChanged(checked: Boolean){
        emailNotiFicationEventListener?.onMessageChanged(checked)
    }



}