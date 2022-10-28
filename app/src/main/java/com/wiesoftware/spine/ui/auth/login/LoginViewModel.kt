package com.wiesoftware.spine.ui.auth.login

import android.view.View
import androidx.lifecycle.ViewModel
import com.wiesoftware.spine.data.repo.AuthRepositry
import com.wiesoftware.spine.util.ApiException
import com.wiesoftware.spine.util.Coroutines
import com.wiesoftware.spine.util.NoInternetException
import org.json.JSONObject

class LoginViewModel(
    private val authRepositry: AuthRepositry
):ViewModel() {
    var email:String?=null
    var password:String?=null
    var loginEventListener: LoginEventListener?=null
    var isVisibles:Boolean=false


    fun loginButtonClicked(view: View){
        isVisibles=true
        if (email.isNullOrEmpty() || password.isNullOrEmpty()){
            loginEventListener?.onLoginFailed("Please enter credentials.")
            isVisibles=false
            return
        }
        Coroutines.main {
            try {
                val response= authRepositry.userLogin(email!!,password!!)
                val msg: String=response.message!!
                if (msg.contains("Your account is not verify",true) ||
                    msg.contains("Login successfully",true)){
//                    response.data?.let {
                        var isVerified: Boolean=false
                        if (msg.contains("Your account is not verify",true)){
                            isVerified=false
                        }else{
                            isVerified=true
                        }
                        isVisibles=false
                        loginEventListener?.onLoginSuccess(isVerified, response.token, response.token_type)
//                        //authRepositry.saveUser(it)
//                        return@main
//                    }
                }else{
                    isVisibles=false
                    loginEventListener?.onLoginFailed("Opps! Invalid credentials.")
                }
            }catch (e: ApiException){
                isVisibles=false
             loginEventListener?.onLoginFailed(e.message!!)
            }catch (e: NoInternetException){
                isVisibles=false
                loginEventListener?.onLoginFailed(e.message!!)
            }
        }

    }


    fun getUserDetails() {
        Coroutines.main {
            try {
                val response= authRepositry.getUserDetails()
                val msg: String=response.message
//                if (msg.contains("Your account is not verify",true) ||
//                    msg.contains("Login successfully",true)){
                    loginEventListener?.onUserDetailsSuccess(response.data)
//                }else{
//                    isVisibles=false
//                    loginEventListener?.onLoginFailed("Opps! Invalid credentials.")
//                }
            }catch (e: ApiException){
                isVisibles=false
                loginEventListener?.onLoginFailed(e.message!!)
            }catch (e: NoInternetException){
                isVisibles=false
                loginEventListener?.onLoginFailed(e.message!!)
            }
        }
    }

    fun backButtonClicked(view: View){
        loginEventListener?.onBack()
    }
    fun forgotButtonClicked(view: View){
        loginEventListener?.onForgotPassword("")
        /*isVisibles=true
        if (email.isNullOrEmpty()){
            loginEventListener?.onLoginFailed("Please enter email.")
            isVisibles=false
            return
        }
        Coroutines.main {
            try {
                val response= authRepositry.forgotPassword(email!!)
                val re: String=response.string()
                val res=JSONObject(re)
                loginEventListener?.onForgotPassword(res.getString("message"))
                isVisibles=false
            }catch (e: ApiException){
                isVisibles=false
                loginEventListener?.onLoginFailed(e.message!!)
            }catch (e: NoInternetException){
                isVisibles=false
                loginEventListener?.onLoginFailed(e.message!!)
            }
        }*/
    }
    fun facebookButtonClicked(view: View){
        loginEventListener?.onFacebookLogin()
    }
}