package com.wiesoftware.spine.ui.home.menus.profile.setting.account_settings.language

import android.view.View
import androidx.lifecycle.ViewModel
import com.wiesoftware.spine.data.repo.HomeRepositry

/**
 * Created by Vivek kumar on 2/5/2021.
 * Email: vivekpcst.kumar@gmail.com.
 */
class SelectLanguageViewmodel(val homeRepositry: HomeRepositry): ViewModel() {

    var selectLanguageEventListener: SelectLanguageEventListener?=null

    fun getLoggedInUser()=homeRepositry.getUser()
    fun onBack(view: View){
        selectLanguageEventListener?.onBack()
    }

}