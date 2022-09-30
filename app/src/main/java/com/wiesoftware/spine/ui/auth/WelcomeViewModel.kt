package com.wiesoftware.spine.ui.auth

import android.view.View
import androidx.lifecycle.ViewModel
import com.wiesoftware.spine.data.repo.AuthRepositry

class WelcomeViewModel(
    private val authRepositry: AuthRepositry
) : ViewModel() {

    fun getLoggedInUser()=authRepositry.getUser()

    var welcomeEventListener:WelcomeEventListener? = null

    fun onRegisterButtonClicked(view : View){
        welcomeEventListener?.onRegister()
    }
    fun onLoginButtonClicked(view: View){
        welcomeEventListener?.onLogin()
    }
}