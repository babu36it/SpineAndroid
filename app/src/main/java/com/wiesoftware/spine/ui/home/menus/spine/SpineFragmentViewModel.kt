package com.wiesoftware.spine.ui.home.menus.spine

import android.view.View
import androidx.lifecycle.ViewModel

/**
 * Created by Vivek kumar on 8/12/2020.
 * E-mail:- vivekpcst.kumar@gmail.com
 */
class SpineFragmentViewModel : ViewModel() {

    var spineFragmentEventListner: SpineFragmentEventListener?=null

    fun onAddBtnClick(view: View){
    spineFragmentEventListner?.onAddButtonClicked()
    }
    fun onSearchBtnClick(view: View){
    spineFragmentEventListner?.onSearchButtonClicked()
    }
    fun closeSearchview(view: View){
    spineFragmentEventListner?.closeSearchView()
    }
}