package com.wiesoftware.spine.ui.auth.otp

import android.view.View
import androidx.lifecycle.ViewModel
import com.wiesoftware.spine.data.repo.AuthRepositry

class OtpViewModel(
    private val authRepositry: AuthRepositry
) : ViewModel() {
    var otpEventListener: OtpEventListener?=null

    fun getLoggedInUser() = authRepositry.getUser()

    fun onTextChanged(s: CharSequence,start: Int,before: Int, count: Int){
            if(s.length == 4) {
                otpEventListener?.onOtpVerified(s.toString())

            }
    }

    fun getCodeAgain(view: View){
        otpEventListener?.getCodeAgain()
    }

    fun onBackButtonClick(view:View){
        otpEventListener?.onBack()
    }

    fun getCodeViaSMSButtonClick(view: View){
        otpEventListener?.getCodeViaSMS()
    }
}