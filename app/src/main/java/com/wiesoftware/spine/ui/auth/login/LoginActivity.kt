package com.wiesoftware.spine.ui.auth.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.wiesoftware.spine.R
import com.wiesoftware.spine.data.db.entities.User
import com.wiesoftware.spine.data.repo.AuthRepositry
import com.wiesoftware.spine.databinding.ActivityLoginBinding
import com.wiesoftware.spine.ui.auth.otp.OtpActivity
import com.wiesoftware.spine.ui.auth.fb.FbEmailActivity
import com.wiesoftware.spine.ui.auth.fb.fbId
import com.wiesoftware.spine.ui.auth.forgotpassword.ForgotPasswordActivity
import com.wiesoftware.spine.ui.auth.register.RegisterActivity
import com.wiesoftware.spine.ui.home.HomeActivity
import com.wiesoftware.spine.ui.home.menus.profile.setting.account_settings.AccountSettingsActivity
import com.wiesoftware.spine.util.*
import kotlinx.coroutines.launch
import org.json.JSONException
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance
import java.util.*

class LoginActivity : AppCompatActivity(), LoginEventListener, KodeinAware {
    lateinit var callbackManager: CallbackManager

    override val kodein by kodein()
    private val factory: LoginViewModelFactory by instance()
    lateinit var binding: ActivityLoginBinding
    lateinit var viewModel: LoginViewModel
    val authRepositry: AuthRepositry by instance()

    var isFromLoginAPI: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        viewModel = ViewModelProvider(this, factory).get(LoginViewModel::class.java)
        binding.viewmodel = viewModel
        viewModel.loginEventListener = this
        callbackManager = CallbackManager.Factory.create()
    }

    override fun onForgotPassword(string: String) {
        startActivity(Intent(this, ForgotPasswordActivity::class.java))
    }

    override fun onFacebookLogin() {
        viewModel.isVisibles = true
        val accessToken = AccessToken.getCurrentAccessToken()
        val isLoggedIn = accessToken != null && !accessToken.isExpired
        if (isLoggedIn) {
            getFbData(accessToken)
        } else {
            LoginManager.getInstance()
                .logInWithReadPermissions(this, Arrays.asList("public_profile", "email"))
            LoginManager.getInstance()
                .registerCallback(callbackManager, object : FacebookCallback<LoginResult?> {
                    override fun onSuccess(result: LoginResult?) {
                        result?.accessToken?.let {
                            getFbData(it)
                        }
                    }

                    override fun onCancel() {
                        toast("Facebook login cancelled")
                        viewModel.isVisibles = false
                    }

                    override fun onError(error: FacebookException?) {
                        toast(error?.message!!)
                        viewModel.isVisibles = false
                    }

                })
        }
    }

    override fun onBack() {
        onBackPressed()
    }

    override fun onLoginFailed(msg: String) {
        toast(msg)
    }


    override fun onLoginSuccess(isVerified: Boolean, token: String, tokenType: String) {
        isFromLoginAPI = isVerified
        Prefs.putAny("AuthToken", token)
        viewModel.getUserDetails()
    }

    override fun onUserDetailsSuccess(user: User) {
        toast(user.verification_pin!!)

        lifecycleScope.launch {
            try {
                authRepositry.saveUser(user)
                if(user.verify_email == "1") {
                    val intent = Intent(this@LoginActivity, HomeActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                } else {
                    val intent = Intent(this@LoginActivity, OtpActivity::class.java)
                    intent.putExtra(RegisterActivity.REGISTERED_USER, user)
                    startActivity(intent)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    }


    private fun getFbData(accessToken: AccessToken) {
        val request = GraphRequest.newMeRequest(
            accessToken
        ) { `object`, response ->
            try {
                Log.e("fbobj:", "" + `object`)
                val name = `object`.getString("name")
                val id = `object`.getString("id")
                val email = `object`.getString("email")
                loginWithFb(name, id, email)
//                toast(name)
//                val intent=Intent(this, FbEmailActivity::class.java)
//                intent.putExtra("name",name)
//                intent.putExtra("fbId",id)
//                startActivity(intent)
                viewModel.isVisibles = false

            } catch (e: JSONException) {
                e.printStackTrace()
                viewModel.isVisibles = false
            }
        }
        val parameters = Bundle()
        parameters.putString("fields", "id,name,link,email")
        request.parameters = parameters
        request.executeAsync()
    }

    private fun loginWithFb(name: String, id: String, email: String) {
        lifecycleScope.launch {
            try {
                val response = authRepositry.socialLogin(name, email, id)
                Log.e("fbId::", "" + id)
                Log.e("fb::", "" + response)
                if (response.status) {
                    response.data.let {
                        if (it.facebook_id == null) {
                            "Oops! Login failed".toast(this@LoginActivity)
                            return@launch
                        }
                        onLoginSuccess( true, response.token, response.token_type)
                        //authRepositry.saveUser(it)
                        return@launch
                    }
                } else {
                    onLoginFailed(response.message)
                }

            } catch (e: ApiException) {
                e.printStackTrace()
                onLoginFailed(e.message!!)
            } catch (e: NoInternetException) {
                e.printStackTrace()
                onLoginFailed(e.message!!)
            } catch (e: Exception) {
                e.printStackTrace()
                onLoginFailed(e.message!!)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }

}