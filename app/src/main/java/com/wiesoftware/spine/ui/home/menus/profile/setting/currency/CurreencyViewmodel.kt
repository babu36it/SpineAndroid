package com.wiesoftware.spine.ui.home.menus.profile.setting.currency

import android.view.View
import androidx.lifecycle.ViewModel
import com.wiesoftware.spine.data.repo.HomeRepository

/**
 * Created by Vivek kumar on 2/11/2021.
 * Email: vivekpcst.kumar@gmail.com.
 */
class CurreencyViewmodel(val homeRepositry: HomeRepository) : ViewModel() {

    var currencyEventListener: CurrencyEventListener? =null

    fun getLoggedInUser()=homeRepositry.getUser()

    fun onBack(view: View){
        currencyEventListener?.onBack()
    }
}