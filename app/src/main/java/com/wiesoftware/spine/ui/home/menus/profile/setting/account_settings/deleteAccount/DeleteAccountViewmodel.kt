package com.wiesoftware.spine.ui.home.menus.profile.setting.account_settings.deleteAccount

import android.view.View
import androidx.lifecycle.ViewModel
import com.wiesoftware.spine.data.repo.HomeRepository

/**
 * Created by Vivek kumar on 3/4/2021.
 * Email: vivekpcst.kumar@gmail.com.
 */
class DeleteAccountViewmodel(val homeRepositry: HomeRepository): ViewModel() {

    var deleteAccountEventListener: DeleteAccountEventListener? =null

    fun getLoggedInUser() = homeRepositry.getUser()

    fun onBack(view: View){
        deleteAccountEventListener?.onBack()
    }
    fun onDelete(view: View){
        deleteAccountEventListener?.onDelete()
    }
}