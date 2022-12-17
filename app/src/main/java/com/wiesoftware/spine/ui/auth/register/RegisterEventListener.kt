package com.wiesoftware.spine.ui.auth.register

import android.view.View
import com.wiesoftware.spine.data.db.entities.User

interface RegisterEventListener {
    fun onBack()
    fun onRegistrationStart()
    fun onRegistrationSuccess(signupResponse: User?)
    fun onRegistrationFailed(s: String)
    fun onCatSelection()
    fun onRegistration(userName: String, email: String, password: String, town: String)
    fun onPrivacyPolicyClick()
    fun onServiceClick()
    fun onFacebookSignup()
}