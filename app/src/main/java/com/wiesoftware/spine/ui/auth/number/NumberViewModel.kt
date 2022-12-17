package com.wiesoftware.spine.ui.auth.number

import android.view.View
import androidx.lifecycle.ViewModel
import com.wiesoftware.spine.data.repo.AuthRepository

class NumberViewModel(
    private val authRepositry: AuthRepository
) : ViewModel() {
    var numberEventListener: NumberEventListener?=null
    var phone_number:String?=null

    fun getLoggedInUser()= authRepositry.getUser()

    fun onBackButtonClick(view:View){
        numberEventListener?.onBack()
    }
    fun onNextButtonClick(view: View){
        if(phone_number.isNullOrEmpty()){
            numberEventListener?.onCodeFailed("Please Enter a number")
            return
        }
       numberEventListener?.onNext(phone_number!!)
    }
}