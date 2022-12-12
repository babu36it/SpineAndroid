package com.wiesoftware.spine.ui.auth.fb

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.wiesoftware.spine.R
import com.wiesoftware.spine.data.db.entities.User
import com.wiesoftware.spine.data.repo.AuthRepository
import com.wiesoftware.spine.databinding.ActivityFbEmailBinding
import com.wiesoftware.spine.ui.auth.otp.OtpActivity
import com.wiesoftware.spine.ui.auth.register.RegisterActivity
import com.wiesoftware.spine.ui.home.HomeActivity
import com.wiesoftware.spine.util.*
import kotlinx.android.synthetic.main.activity_fb_email.*
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

var name: String?= null
var fbId: String?= null

class FbEmailActivity : AppCompatActivity(),KodeinAware, FbEmailEventListener {
    override val kodein by kodein()
    val authRepositry: AuthRepository by instance()
    val factory: FbEmailViewModelFactory by instance()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityFbEmailBinding=DataBindingUtil.setContentView(this,R.layout.activity_fb_email)
        val viewmodel=ViewModelProvider(this,factory).get(FbEmailViewModel::class.java)
        binding.viewmodel=viewmodel
        viewmodel.fbEmailEventListener=this
        name =intent.getStringExtra("name")
        fbId =intent.getStringExtra("fbId")

    }

    override fun onBack() {
        onBackPressed()
    }

    override fun onLoginInit() {
        pbEmail.show()
    }

    override fun onLoginFailed(msg: String) {
        toast(msg)
        pbEmail.hide()
    }

    override fun onLoginStart(email: String) {
        Coroutines.main {
            try {
                val response=authRepositry.socialLogin(name!!,email, fbId!!)
                Log.e("fbId::", ""+fbId)
                Log.e("fb::",""+response)
                if (response.status){
                    response.data.let {
                        onLoginSuccess(it,true)
                        //authRepositry.saveUser(it)
                        return@main
                    }
                    onLoginFailed("${response.message}")
                }else{
                    onLoginFailed("${response.message}")
                }

            }catch (e: ApiException){
               e.printStackTrace()
               onLoginFailed(e.message!!)
            }catch (e: NoInternetException){
                e.printStackTrace()
                onLoginFailed(e.message!!)
            }catch (e: Exception){
                e.printStackTrace()
                onLoginFailed(e.message!!)
            }
        }
    }

    override fun onLoginSuccess(user: User, isVerified: Boolean) {
        if (isVerified){
            pbEmail.hide()
            lifecycleScope.launch {
                try {
                    authRepositry.saveUser(user)
                    val intent = Intent(this@FbEmailActivity, HomeActivity::class.java)
                    intent.flags= Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                }catch (e: Exception){
                    e.printStackTrace()
                }
            }
        }else{
            pbEmail.hide()
            toast(user.verification_pin!!)
            val intent = Intent(this, OtpActivity::class.java)
            intent.putExtra(RegisterActivity.REGISTERED_USER,user)
            startActivity(intent)
        }
    }
}