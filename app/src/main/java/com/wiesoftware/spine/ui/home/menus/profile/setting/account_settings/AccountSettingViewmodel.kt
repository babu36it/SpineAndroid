package com.wiesoftware.spine.ui.home.menus.profile.setting.account_settings

import android.view.View
import androidx.lifecycle.ViewModel
import com.wiesoftware.spine.data.repo.HomeRepositry

/**
 * Created by Vivek kumar on 1/21/2021.
 * Email: vivekpcst.kumar@gmail.com.
 */
class AccountSettingViewmodel(val homeRepositry: HomeRepositry): ViewModel() {

    var accountSettingEventListener: AccountSettingEventListener? = null

    fun getLoggedInUser()=homeRepositry.getUser()

    fun onBack(view: View){
        accountSettingEventListener?.onBack()
    }

    fun deleteAccount(view: View){
        accountSettingEventListener?.onDeleteAccount()
    }
    fun changePassword(view: View){
        accountSettingEventListener?.onChangePassword()
    }
    fun onChangeEmail(view: View){
        accountSettingEventListener?.onChangeEmail()
    }
    fun onMessaging(view: View){
        accountSettingEventListener?.onMessaging()
    }
    fun onSaveCalendarEvent(view: View){
        accountSettingEventListener?.onSaveCalendarEvent()
    }
    fun onLanguageSelect(view: View){
        accountSettingEventListener?.onLanguageSelect()
    }
    fun onCurrency(view: View){
        accountSettingEventListener?.onCurrency()
    }
    fun onCheckedChanged(checked: Boolean){
        accountSettingEventListener?.onDarkMode(checked)
    }
    fun onDeactivateAccount(view: View){
        accountSettingEventListener?.onDeactivateAccount()
    }

}