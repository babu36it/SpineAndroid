package com.wiesoftware.spine.ui.home.menus.spine.welcome

import android.view.View
import androidx.lifecycle.ViewModel
import com.wiesoftware.spine.data.repo.HomeRepository
/**
 * Created by Vivek kumar on 10/21/2020.
 * E-mail:- vivekpcst.kumar@gmail.com
 */
class ViewWelcomeViewModel(private  val homeRepositry: HomeRepository): ViewModel(){
    var viewWelcomeEventListener: ViewWelcomeEventListener?=null
    fun onBack(view: View){
        viewWelcomeEventListener?.onBack()
    }
}