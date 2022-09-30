package com.wiesoftware.spine.ui.auth.otp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.wiesoftware.spine.R
import com.wiesoftware.spine.data.db.entities.User
import com.wiesoftware.spine.data.repo.AuthRepositry
import com.wiesoftware.spine.databinding.ActivityOtpBinding
import com.wiesoftware.spine.ui.auth.number.NumberActivity
import com.wiesoftware.spine.ui.auth.register.RegisterActivity
import com.wiesoftware.spine.ui.home.HomeActivity
import com.wiesoftware.spine.util.*
import kotlinx.android.synthetic.main.activity_otp.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

var user_id: String = ""
var verification_pin: String = ""
var phoneNumber: String? = null

class OtpActivity : AppCompatActivity(), OtpEventListener, KodeinAware {
    override val kodein by kodein()

    private val factory: OtpViewModelFactory by instance()
    private val authRepository: AuthRepositry by instance()
    lateinit var binding: ActivityOtpBinding
    lateinit var userRes: User
    var enteredotp: String = ""
    var verificationId: String = ""

    private var mAuth: FirebaseAuth? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_otp)
        val viewModel = ViewModelProvider(this, factory).get(OtpViewModel::class.java)
        binding.viewmodel = viewModel
        viewModel.otpEventListener = this

        mAuth = Firebase.auth
        /*viewModel.getLoggedInUser().observe(this, Observer {user->
            if (user != null){
                tvOtpUser.text= user.email
                user_id = user.users_id.toString()
                verification_pin =user.verification_pin.toString()
            }
        })*/
        getRegisteredUserDetails()

        setOtpListener()

    }

    private fun setOtpListener() {

        binding.editTextNumber.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s?.length == 1) {

                    enteredotp = s.toString()
                    binding.editTextNumber.clearFocus()
                    binding.editTextNumber1.requestFocus()
                    binding.editTextNumber1.isCursorVisible = true
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })
        binding.editTextNumber1.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s?.length == 1) {
                    enteredotp = enteredotp + s.toString()
                    binding.editTextNumber1.clearFocus()
                    binding.editTextNumber3.requestFocus()
                    binding.editTextNumber3.isCursorVisible = true
                } else {
                    binding.editTextNumber.requestFocus()
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })
        binding.editTextNumber3.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s?.length == 1) {
                    enteredotp = enteredotp + s.toString()
                    binding.editTextNumber3.clearFocus()
                    binding.editTextNumber4.requestFocus()
                    binding.editTextNumber4.isCursorVisible = true
                } else {
                    binding.editTextNumber1.requestFocus()
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })
        binding.editTextNumber4.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s?.length == 1) {
                    enteredotp = enteredotp + s.toString()
                    binding.editTextNumber4.clearFocus()
                    binding.editTextNumber5.requestFocus()
                    binding.editTextNumber5.isCursorVisible = true
                } else {
                    binding.editTextNumber3.requestFocus()
                }
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })

        binding.editTextNumber5.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s?.length == 1) {
                    enteredotp = enteredotp + s.toString()
                    binding.editTextNumber5.clearFocus()
                    binding.editTextNumber6.requestFocus()
                    binding.editTextNumber6.isCursorVisible = true
                } else {
                    binding.editTextNumber4.requestFocus()
                }
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })

        binding.editTextNumber6.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s?.length == 1) {
                    enteredotp = enteredotp + s.toString()
                    onOtpVerified(enteredotp)
                    //enteredotp.toast(this@OtpActivity)
                } else {
                    binding.editTextNumber3.requestFocus()
                }
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })

        // var otp=binding.otpUser.text.toString()

    }

    private fun getRegisteredUserDetails() {
        userRes = intent.getSerializableExtra(RegisterActivity.REGISTERED_USER) as User
        Log.e("usergfff:", "" + userRes)
        tvOtpUser.text = userRes.email
        phoneNumber = intent.getStringExtra("phone")

        if (intent.getStringExtra("verificationId") != null)
            verificationId = intent.getStringExtra("verificationId").toString()

        if (!phoneNumber.isNullOrEmpty()) {
            tvOtpUser.text = phoneNumber
            binding.textView11.text = getString(R.string.verification_on_number)
        }
        user_id = userRes.users_id.toString()
        verification_pin = userRes.verification_pin.toString()
    }

    override fun onBack() {
        onBackPressed()
    }

    override fun getCodeViaSMS() {
        val intent = Intent(this, NumberActivity::class.java)
        intent.putExtra(RegisterActivity.REGISTERED_USER, userRes)
        startActivity(intent)
    }

    override fun onOtpVerified(otp: String) {

        if (!verificationId.isNullOrEmpty()) {
            pbOtp.show()
            val credential = PhoneAuthProvider.getCredential(verificationId!!, otp)
            mAuth!!.signInWithCredential(credential)
                .addOnCompleteListener(this) { task ->
                    pbOtp.hide()
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Coroutines.main {
                            try {
                                val response = authRepository.verifyUser(user_id)
                                if (response.status!!) {
                                    toast("${response.message}")
                                    try {
                                        authRepository.saveUser(userRes)
                                    } catch (e: Exception) {
                                        Log.e("nidilof", e.toString())
                                    }

                                    val intent = Intent(this, HomeActivity::class.java)
                                    intent.flags =
                                        Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                    startActivity(intent)
                                } else {
                                    toast("${response.message}")
                                }
                                pbOtp.hide()
                            } catch (e: ApiException) {
                                toast(e.message!!)
                                pbOtp.hide()
                            } catch (e: NoInternetException) {
                                toast(e.message!!)
                                pbOtp.hide()
                            }
                        }
                    } else {
                        if (task.exception is FirebaseAuthInvalidCredentialsException) {
                            // The verification code entered was invalid
                            toast("The verification code entered was invalid")
                        }
                    }
                }


        } else if (otp.equals(verification_pin)) {
            pbOtp.show()
            Coroutines.main {
                try {
                    val response = authRepository.verifyUser(user_id)
                    if (response.status!!) {
                        toast("${response.message}")
                        try {
                            authRepository.saveUser(userRes)
                        } catch (e: Exception) {
                            Log.e("nidilof", e.toString())
                        }

                        val intent = Intent(this, HomeActivity::class.java)
                        intent.flags =
                            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                    } else {
                        toast("${response.message}")
                    }
                    pbOtp.hide()
                } catch (e: ApiException) {
                    toast(e.message!!)
                    pbOtp.hide()
                } catch (e: NoInternetException) {
                    toast(e.message!!)
                    pbOtp.hide()
                }
            }

        } else {
            toast("Otp didn't match")
        }

    }

    override fun getCodeAgain() {

            pbOtp.show()

            Coroutines.main {
                try {
                    val response = authRepository.reSendCode(user_id)
                    if (response.status!!) {
                        response.data?.let {
                            //authRepository.saveUser(it)

//                            val intent=Intent(this, OtpActivity::class.java)
//                            intent.putExtra("phone", phoneNumber)
//                            startActivity(intent)
                        }
                        toast("Code sent successfully")
                    } else {
                        toast(response.message!!)
                    }
                    pbOtp.hide()
                } catch (e: ApiException) {
                    toast(e.message!!)
                    pbOtp.hide()
                } catch (e: NoInternetException) {
                    toast(e.message!!)
                    pbOtp.hide()
                }
            }
    }
}