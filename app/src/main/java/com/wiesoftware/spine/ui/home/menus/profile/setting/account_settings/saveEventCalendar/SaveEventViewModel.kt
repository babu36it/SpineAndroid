package com.wiesoftware.spine.ui.home.menus.profile.setting.account_settings.saveEventCalendar

import android.view.View
import androidx.lifecycle.ViewModel
import com.wiesoftware.spine.data.repo.HomeRepository

/**
 * Created by Vivek kumar on 1/28/2021.
 * Email: vivekpcst.kumar@gmail.com.
 */
class SaveEventViewModel(val homeRepositry: HomeRepository): ViewModel() {

    var saveEventEventListener: SaveEventEventListener?=null

    fun getLoggedInUser() = homeRepositry.getUser()

    fun onBack(view: View){
        saveEventEventListener?.onBack()
    }
    fun  onCheckedChanged(isChecked: Boolean){
        saveEventEventListener?.onCheckedChanged(isChecked)
    }
}