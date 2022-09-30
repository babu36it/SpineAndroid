package com.wiesoftware.spine.ui.auth.forgotpassword

import android.view.View
import androidx.lifecycle.ViewModel

/**
 * Created by Vivek kumar on 6/21/2021.
 * E-mail:- vivekpcst.kumar@gmail.com
 */
class ForgotPasswordViewmodel: ViewModel() {

    var forgotPasswordEventListener: ForgotPasswordEventListener? = null

    fun onBack(view: View){
        forgotPasswordEventListener?.onBack()
    }

    fun onForgotPassword(view: View){
        forgotPasswordEventListener?.onForgotPassword()
    }
}