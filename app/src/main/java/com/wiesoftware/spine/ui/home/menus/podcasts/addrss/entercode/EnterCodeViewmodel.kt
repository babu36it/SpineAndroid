package com.wiesoftware.spine.ui.home.menus.podcasts.addrss.entercode

import android.view.View
import androidx.lifecycle.ViewModel

/**
 * Created by Vivek kumar on 4/6/2021.
 * Email: vivekpcst.kumar@gmail.com.
 */
class EnterCodeViewmodel(): ViewModel() {
    var code:String?=null
    var enterCodeEventListener:EnterCodeEventListener? = null
    fun onBack(viewModel: View){
        enterCodeEventListener?.onBack()
    }
    fun onNext(view: View){
        if (code.isNullOrEmpty()){
            return
        }
        enterCodeEventListener?.onNext(code!!)
    }
    fun onResendCode(view: View){
        enterCodeEventListener?.onResendCode()
    }

}