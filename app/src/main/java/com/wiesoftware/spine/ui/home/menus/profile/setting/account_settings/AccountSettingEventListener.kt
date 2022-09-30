package com.wiesoftware.spine.ui.home.menus.profile.setting.account_settings

/**
 * Created by Vivek kumar on 1/21/2021.
 * Email: vivekpcst.kumar@gmail.com.
 */
interface AccountSettingEventListener {

    fun onBack()
    fun onChangePassword()
    fun onDeleteAccount()
    fun onChangeEmail()
    fun onMessaging()
    fun onSaveCalendarEvent()
    fun onLanguageSelect()
    fun onCurrency()
    fun onDarkMode(isChecked: Boolean)
    fun onDeactivateAccount()
}