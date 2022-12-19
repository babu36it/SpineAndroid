package com.wiesoftware.spine.ui.home.menus.profile.setting.help.privacy

import android.view.View
import androidx.lifecycle.ViewModel
import com.wiesoftware.spine.data.repo.HomeRepository

/**
 * Created by Vivek kumar on 2/22/2021.
 * Email: vivekpcst.kumar@gmail.com.
 */
class PrivacySettingViewModel(val homeRepositry: HomeRepository) : ViewModel() {
    var privacySettingEventListener: PrivacySettingEventListener? = null

    fun getLoggedInUser() = homeRepositry.getUser()
    fun onBack(view: View) {
        privacySettingEventListener?.onBack()
    }

    fun onPrivacyPolicy(view: View) {
        privacySettingEventListener?.onPrivacyPolicy()
    }

    fun findabilityOnCheckedChanged(isChecked: Boolean) {
        privacySettingEventListener?.findabilityOnchanged(isChecked)
    }

    fun personalizedOnCheckedChanged(isChecked: Boolean) {
        privacySettingEventListener?.personalizedOnChanged(isChecked)
    }

    fun customizationOnCheckedChanged(isChecked: Boolean) {
        privacySettingEventListener?.customizationOnChanged(isChecked)
    }

    fun necessaryOnCheckedChanged(isChecked: Boolean) {
        privacySettingEventListener?.necessaryOnChanged(isChecked)
    }
}