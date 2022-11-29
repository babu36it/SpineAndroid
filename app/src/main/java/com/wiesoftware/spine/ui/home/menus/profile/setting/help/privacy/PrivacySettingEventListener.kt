package com.wiesoftware.spine.ui.home.menus.profile.setting.help.privacy

/**
 * Created by Vivek kumar on 2/22/2021.
 * Email: vivekpcst.kumar@gmail.com.
 */
interface PrivacySettingEventListener {
    fun onBack()
    fun onPrivacyPolicy()
    fun findabilityOnchanged(isChecked: Boolean)
    fun personalizedOnChanged(isChecked: Boolean)
    fun customizationOnChanged(isChecked: Boolean)
    fun necessaryOnChanged(isChecked: Boolean)
}