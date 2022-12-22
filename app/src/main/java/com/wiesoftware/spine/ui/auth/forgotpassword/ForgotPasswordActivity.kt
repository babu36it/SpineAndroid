package com.wiesoftware.spine.ui.auth.forgotpassword

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.wiesoftware.spine.R
import com.wiesoftware.spine.data.repo.AuthRepository
import com.wiesoftware.spine.databinding.ActivityForgotPasswordBinding
import com.wiesoftware.spine.util.toast
import kotlinx.coroutines.launch
import org.json.JSONObject
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class ForgotPasswordActivity : AppCompatActivity(), KodeinAware, ForgotPasswordEventListener {

    override val kodein by kodein()
    lateinit var binding: ActivityForgotPasswordBinding
    val authRepository: AuthRepository by instance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=DataBindingUtil.setContentView(this,R.layout.activity_forgot_password)
        val viewmodel = ViewModelProvider(this).get(ForgotPasswordViewmodel::class.java)
        binding.viewmodel = viewmodel
        viewmodel.forgotPasswordEventListener = this
    }

    override fun onBack() {
        onBackPressed()
    }

    override fun onForgotPassword() {
        val  email = binding.etEmail.text.toString()
        if (email.isEmpty()){
            return
        }
        lifecycleScope.launch {
            try {
                val response= authRepository.forgotPassword(email)
                val re: String=response.string()
                val res= JSONObject(re)
                if (res.getBoolean("status")){
                    toast("Email sent successfully.")
                }else {
                    toast(res.getString("message"))
                }
            }catch (e: Exception){
                e.printStackTrace()
                toast("Oops! Server not responding. Try again later.")
            }
        }
    }
}