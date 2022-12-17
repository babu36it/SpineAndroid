package com.wiesoftware.spine.ui.auth.fb

import com.wiesoftware.spine.data.db.entities.User

/**
 * Created by Vivek kumar on 8/10/2020.
 * E-mail:- vivekpcst.kumar@gmail.com
 */
interface FbEmailEventListener {
    fun onBack()
    fun onLoginInit()
    fun onLoginFailed(msg: String)
    fun onLoginStart(email: String)
    fun onLoginSuccess(user: User, isVerified: Boolean)
}