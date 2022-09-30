package com.wiesoftware.spine.ui.home.menus.profile.setting.account_settings.deactiveaccount

import android.view.View
import androidx.lifecycle.ViewModel
import com.wiesoftware.spine.data.repo.HomeRepositry

/**
 * Created by Vivek kumar on 2/5/2021.
 * Email: vivekpcst.kumar@gmail.com.
 */
class DeactivateViewmodel(val homeRepositry: HomeRepositry): ViewModel() {

    var deactivateEventListener: DeactivateEventListener?= null
    fun getloggedInUser()=homeRepositry.getUser()

    fun onBack(view: View){
        deactivateEventListener?.onBack()
    }
    fun onDeactivateAccount(view: View){
        deactivateEventListener?.onDeactivateAccount()
    }

    fun onDeleteAccount(view: View){
        deactivateEventListener?.onDeleteAccount()
    }
}