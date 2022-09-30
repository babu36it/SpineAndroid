package com.wiesoftware.spine.ui.home.menus.profile.setting.notifications

import android.view.View
import androidx.lifecycle.ViewModel
import com.wiesoftware.spine.data.repo.HomeRepositry

/**
 * Created by Vivek kumar on 1/21/2021.
 * Email: vivekpcst.kumar@gmail.com.
 */
class NotificationsViewmodel(val homeRepositry: HomeRepositry): ViewModel() {
    var notificationsEventListener: NotificationsEventListener?= null

    fun getLoggedInUser()=homeRepositry.getUser()

    fun onBack(view: View){
        notificationsEventListener?.onBack()
    }
}