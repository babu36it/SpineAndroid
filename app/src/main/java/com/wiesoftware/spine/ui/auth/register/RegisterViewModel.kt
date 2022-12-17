package com.wiesoftware.spine.ui.auth.register

import android.view.View
import androidx.lifecycle.ViewModel
import com.wiesoftware.spine.data.repo.AuthRepository
import com.wiesoftware.spine.util.Utils
import com.wiesoftware.spine.util.Utils.Companion.isValidPassword

class RegisterViewModel(
    private val authRepositry: AuthRepository
) : ViewModel() {

    var registerEventListener: RegisterEventListener? = null
    var userName: String? = null
    var email: String? = null
    var password: String? = null
    var town: String? = null

    fun onBackButtonClicked(view: View) {
        registerEventListener?.onBack()
    }

    fun getLoggedInUser() = authRepositry.getUser()

    fun onCatSelection(view: View) {
        registerEventListener?.onCatSelection()
    }

    fun onRegisterButtonClicked(view: View) {

        if (userName.isNullOrEmpty()) {
            Utils.showToast(view.context, "Enter Name")
        }
        else if (!Utils.isValidEmail(email)) {
            Utils.showToast(view.context, "Enter Valid Email Id")
        }
        else if (!isValidPassword(password.toString()) && !(password.toString().length >= 6)) {
            Utils.showToast(
                view.context, "Password must 6 digit should have," +
                        "1 Capital letter, 1 Small letter, 1 Numeric, 1 Special character"
            )
        }
        else if (town.isNullOrEmpty()) {
            Utils.showToast(view.context, "Enter Town")
        }
        else {
            registerEventListener?.onRegistration(userName.toString(),
                email.toString(), password.toString(), town.toString())
        }

      /*  //registerEventListener?.onRegistrationStart()
        if (userName.isNullOrEmpty() || email.isNullOrEmpty() || password.isNullOrEmpty() || town.isNullOrEmpty()) {
            registerEventListener?.onRegistrationFailed("Field is empty")
            return
        }
        if (isValidPassword(password!!) && password!!.length >= 6) {
            registerEventListener?.onRegistration(userName!!, email!!, password!!, town!!)
        } else {
            registerEventListener?.onRegistrationFailed(
                "Password must 6 digit should have," +
                        "1 Capital letter, 1 Small letter, 1 Numeric, 1 Special character"
            )
        }*/
    }

    fun onPrivacyPolicyClick(view: View) {
        registerEventListener?.onPrivacyPolicyClick()
    }

    fun onServiceClick(view: View) {
        registerEventListener?.onServiceClick()
    }

    fun onFacebookSignup(view: View) {
        registerEventListener?.onFacebookSignup()
    }


}