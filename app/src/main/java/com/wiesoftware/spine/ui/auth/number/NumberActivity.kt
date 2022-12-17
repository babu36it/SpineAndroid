package com.wiesoftware.spine.ui.auth.number

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.wiesoftware.spine.R
import com.wiesoftware.spine.data.db.entities.User
import com.wiesoftware.spine.data.repo.AuthRepository
import com.wiesoftware.spine.databinding.ActivityNumberBinding
import com.wiesoftware.spine.ui.auth.otp.OtpActivity
import com.wiesoftware.spine.ui.auth.otp.user_id
import com.wiesoftware.spine.ui.auth.register.RegisterActivity
import com.wiesoftware.spine.util.*
import kotlinx.android.synthetic.main.activity_number.*
import org.kodein.di.android.kodein
import org.kodein.di.KodeinAware
import org.kodein.di.generic.instance
import java.util.concurrent.TimeUnit

class NumberActivity : AppCompatActivity(), NumberEventListener, KodeinAware {

    override val kodein by kodein()
    private val factory: NumberViewModelFactory by instance()
    private val authRepositry: AuthRepository by instance()

    private var strPhoneNumber: String = ""

    var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks? = null
    private var mAuth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding: ActivityNumberBinding=DataBindingUtil.setContentView(this,R.layout.activity_number)
        val viewModel=ViewModelProvider(this,factory).get(NumberViewModel::class.java)
        binding.viewmodel=viewModel
        viewModel.numberEventListener=this
        val userRes=intent.getSerializableExtra(RegisterActivity.REGISTERED_USER) as User
        user_id=userRes.users_id!!

        mAuth = Firebase.auth


        Firebase.auth.signOut()

        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.
//                signInWithPhoneAuthCredential(credential)
                pbNumber.hide()
            }

            override fun onVerificationFailed(e: FirebaseException) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                pbNumber.hide()
                if (e is FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    toast("Invalid request")
                } else if (e is FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    toast("The SMS quota for the project has been exceeded")
                }

                println("Sanjay onVerificationFailed....."+e.toString())

                // Show a message and update the UI
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.

                // Save verification ID and resending token so we can use them later
//                storedVerificationId = verificationId
//                resendToken = token

                val intent=Intent(this@NumberActivity, OtpActivity::class.java)
                intent.putExtra(RegisterActivity.REGISTERED_USER, userRes)
                intent.putExtra("phone", strPhoneNumber)
                intent.putExtra("verificationId", verificationId)
                startActivity(intent)

            }
        }
    }

    override fun onBack() {
        onBackPressed()
    }

    override fun onNext(phoneNumber: String?) {
        pbNumber.show()

//        Coroutines.main {
//            try {
//                val response = authRepositry.sendCode(user_id, phoneNumber!!)
//                if (response.status!!){
//                    response.data?.let {
//                        //authRepositry.saveUser(it)
//                        toast(it.verification_pin!!)
//                        val intent=Intent(this, OtpActivity::class.java)
//                        intent.putExtra(RegisterActivity.REGISTERED_USER,it)
//                        intent.putExtra("phone",phoneNumber)
//                        startActivity(intent)
//                    }
//                }else{
//                    toast(response.message!!)
//                }
//                pbNumber.hide()
//            }catch (e:ApiException){
//                toast(e.message!!)
//                pbNumber.hide()
//            }catch (e:NoInternetException){
//                toast(e.message!!)
//                pbNumber.hide()
//            }
//        }

        strPhoneNumber = "+91 "+phoneNumber!!

        println("Sanjay..."+strPhoneNumber)

        val options = PhoneAuthOptions.newBuilder(mAuth!!)
            .setPhoneNumber(strPhoneNumber)       // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(this)                 // Activity (for callback binding)
            .setCallbacks(callbacks!!)          // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    override fun onCodeFailed(msg: String?) {
        toast(msg!!)
    }

}