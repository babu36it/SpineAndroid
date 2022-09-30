package com.wiesoftware.spine.ui.home.menus.profile.setting.help.privacy

import android.view.View
import androidx.lifecycle.ViewModel
import com.wiesoftware.spine.data.repo.HomeRepositry

/**
 * Created by Vivek kumar on 2/22/2021.
 * Email: vivekpcst.kumar@gmail.com.
 */
class PrivacySettingViewmodel(val homeRepositry: HomeRepositry) : ViewModel() {
    var privacySettingEventListener: PrivacySettingEventListener? = null

    fun getLoggedInUser() = homeRepositry.getUser()
    fun onBack(view: View){
        privacySettingEventListener?.onBack()
    }
    fun onPrivacyPolicy(view: View){
        privacySettingEventListener?.onPrivacyPolicy()
    }


}