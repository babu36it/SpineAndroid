package com.wiesoftware.spine.ui.home.menus.profile.setting.help

import android.view.View
import androidx.lifecycle.ViewModel
import com.wiesoftware.spine.data.repo.HomeRepository

/**
 * Created by Vivek kumar on 2/11/2021.
 * E-mail:- vivekpcst.kumar@gmail.com
 */
class HelpViewModel(val homeRepositry: HomeRepository): ViewModel() {
    var helpEventListener: HelpEventListener?=null

    fun onBack(view: View){
        helpEventListener?.onBack()
    }
    fun onPrivacySettings(view: View){
        helpEventListener?.onPrivacySettings()
    }
    fun onTermsOfService(view: View){
        helpEventListener?.onTermsOfService()
    }
    fun onAboutSpine(view: View){
        helpEventListener?.onAboutSpine()
    }
    fun onCommunityGuideLines(view: View){
        helpEventListener?.onCommunityGuidelines()
    }
    fun giveFeedback(view: View){
        helpEventListener?.onGiveFeedback()
    }
    fun onHelp(view: View){
        helpEventListener?.onHelp()
    }
}