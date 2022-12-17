package com.wiesoftware.spine.ui.home.menus.events.addordup

import android.view.View
import androidx.lifecycle.ViewModel

/**
 * Created by Vivek kumar on 6/7/2021.
 * E-mail:- vivekpcst.kumar@gmail.com
 */
class AddOrDupEventViewModel: ViewModel() {

    var addOrDupEventListener :AddOrDupEventListener? =null

    fun onBack(view: View){
        addOrDupEventListener?.onBack()
    }
    fun onAddNewEvent(view: View){
        addOrDupEventListener?.addNewEvent()
    }
}