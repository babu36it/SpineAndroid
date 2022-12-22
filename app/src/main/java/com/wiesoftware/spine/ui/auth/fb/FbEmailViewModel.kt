package com.wiesoftware.spine.ui.auth.fb

import android.view.View
import androidx.lifecycle.ViewModel
import com.wiesoftware.spine.data.repo.AuthRepository

/**
 * Created by Vivek kumar on 8/10/2020.
 * E-mail:- vivekpcst.kumar@gmail.com
 */
class FbEmailViewModel(
    private val authRepositry: AuthRepository
) : ViewModel() {
    var fbEmailEventListener: FbEmailEventListener?= null
    var email: String?= null

   fun onBackButtonClick(view: View){
       fbEmailEventListener?.onBack()
   }

    fun onNextButtonClick(view: View){
        fbEmailEventListener?.onLoginInit()
        if (email.isNullOrEmpty()){
            fbEmailEventListener?.onLoginFailed("Please enter email.")
            return
        }
        fbEmailEventListener?.onLoginStart(email!!)
    }
}