package com.wiesoftware.spine.ui.home.menus.profile.setting

import android.view.View
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wiesoftware.spine.data.repo.HomeRepositry
import com.wiesoftware.spine.util.Coroutines
import kotlinx.coroutines.launch

/**
 * Created by Vivek kumar on 10/6/2020.
 * E-mail:- vivekpcst.kumar@gmail.com
 */
class SettingsViewmodel(
    val homeRepositry: HomeRepositry
) : ViewModel() {

    fun getUser()= homeRepositry.getUser()

    var settingsEventListener: SettingsEventListener?= null

    fun onBack(view: View){
        settingsEventListener?.onBack()
    }
    fun onProfileEdit(view: View){
        settingsEventListener?.onProfileEdit()
    }
    fun onLogout(view: View){
        settingsEventListener?.onLogout()
        }

    fun onInvite(view: View){
        settingsEventListener?.onInvite()
    }
    fun onAccountSetting(view: View){
        settingsEventListener?.onAccountSettings()
    }
    fun onNotifications(view: View){
        settingsEventListener?.onNotifications()
    }
    fun onHelp(view: View){
        settingsEventListener?.onHelp()
    }
    fun onMyAds(view: View){
        settingsEventListener?.onMyAds()
    }
}