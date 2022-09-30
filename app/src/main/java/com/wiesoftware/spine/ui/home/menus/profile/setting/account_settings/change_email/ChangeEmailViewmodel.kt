package com.wiesoftware.spine.ui.home.menus.profile.setting.account_settings.change_email

import android.view.View
import androidx.lifecycle.ViewModel
import com.wiesoftware.spine.data.repo.HomeRepositry

/**
 * Created by Vivek kumar on 1/27/2021.
 * Email: vivekpcst.kumar@gmail.com.
 */
class ChangeEmailViewmodel(val homeRepositry: HomeRepositry): ViewModel() {
    var changeEmailEventListener: ChangeEmailEventListener?= null

    fun getLoggedInUser() = homeRepositry.getUser()
    fun onBack(view: View){
        changeEmailEventListener?.onBack()
    }

}