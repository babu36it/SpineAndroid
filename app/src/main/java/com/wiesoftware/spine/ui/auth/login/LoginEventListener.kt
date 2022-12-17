package com.wiesoftware.spine.ui.auth.login

import com.wiesoftware.spine.data.db.entities.User

interface LoginEventListener {

    fun onForgotPassword(string: String)
    fun onFacebookLogin()
    fun onBack()
    fun onLoginFailed(msg: String)
    fun onLoginSuccess(isVerified: Boolean, token: String, tokenType: String)
    fun onUserDetailsSuccess(user: User)

}