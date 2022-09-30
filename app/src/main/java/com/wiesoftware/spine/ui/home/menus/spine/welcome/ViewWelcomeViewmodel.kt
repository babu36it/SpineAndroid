package com.wiesoftware.spine.ui.home.menus.spine.welcome

import android.view.View
import androidx.lifecycle.ViewModel
import com.wiesoftware.spine.data.repo.HomeRepositry
/**
 * Created by Vivek kumar on 10/21/2020.
 * E-mail:- vivekpcst.kumar@gmail.com
 */
class ViewWelcomeViewmodel(private  val homeRepositry: HomeRepositry): ViewModel(){
    var viewWelcomeEventListener: ViewWelcomeEventListener?=null
    fun onBack(view: View){
        viewWelcomeEventListener?.onBack()
    }
}