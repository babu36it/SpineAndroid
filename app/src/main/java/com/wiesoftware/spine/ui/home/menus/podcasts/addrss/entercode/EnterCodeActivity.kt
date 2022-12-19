package com.wiesoftware.spine.ui.home.menus.podcasts.addrss.entercode

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.wiesoftware.spine.R
import com.wiesoftware.spine.RuntimeLocaleChanger
import com.wiesoftware.spine.data.repo.HomeRepository
import com.wiesoftware.spine.data.repo.PodcastRepository
import com.wiesoftware.spine.databinding.ActivityEnterCodeBinding
import com.wiesoftware.spine.ui.home.menus.podcasts.addpodcasts.AddPodcastActivity
import com.wiesoftware.spine.util.toast
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class EnterCodeActivity : AppCompatActivity(), KodeinAware, EnterCodeEventListener {

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base?.let { RuntimeLocaleChanger.wrapContext(it) })
    }

    override val kodein by kodein()
    lateinit var binding: ActivityEnterCodeBinding
    val homeRepositry: HomeRepository by instance()
    val podcastRepository: PodcastRepository by instance()

    var email = ""
    var enteredotp: String = ""
    lateinit var progressDialog: ProgressDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_enter_code)
        val viewmodel = ViewModelProvider(this).get(EnterCodeViewModel::class.java)
        binding.viewmodel = viewmodel
        viewmodel.enterCodeEventListener = this
        progressDialog= ProgressDialog(this)
        homeRepositry.getUser().observe(this, Observer { user ->
            email = user.email!!
            binding.tvEmail.text = "(" + email + ")"
            sendCodeOnEmail()
        })
        setOtpListener()

    }

    private fun sendCodeOnEmail() {
        lifecycleScope.launch {
            try {
                showProgressDialog()
                val res = podcastRepository.sendVerificationCodeOnEmail(email)
                if (res.status) {
                    dismissProgressDailog()
                    Toast.makeText(this@EnterCodeActivity, res.otp, Toast.LENGTH_SHORT).show()
                    Toast.makeText(this@EnterCodeActivity, res.message, Toast.LENGTH_SHORT).show()
                }else{
                    dismissProgressDailog()
                    Toast.makeText(this@EnterCodeActivity, res.message, Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                dismissProgressDailog()
            }
        }
    }

    override fun onBack() {
        onBackPressed()
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


                    //enteredotp.toast(this@OtpActivity)
                } else {
                    binding.editTextNumber3.requestFocus()
                }
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })

        // var otp=binding.otpUser.text.toString()

        binding.button98.setOnClickListener {

            if (binding.editTextNumber.text.isNullOrEmpty() || binding.editTextNumber1.text.isNullOrEmpty() ||
                binding.editTextNumber3.text.isNullOrEmpty() || binding.editTextNumber4.text.isNullOrEmpty()
            ) {
                "Enter 4 Digit Your OTP".toast(this@EnterCodeActivity)
            } else {
                onNext(enteredotp)
            }
        }
    }

    override fun onNext(code: String) {

        lifecycleScope.launch {
            try {
                showProgressDialog()
                val res = podcastRepository.verifyEmailVerificationCode(email, code)
                if (res.status) {
                    dismissProgressDailog()
                    Toast.makeText(this@EnterCodeActivity, res.message, Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this@EnterCodeActivity, AddPodcastActivity::class.java))
                } else {
                    dismissProgressDailog()
                    Toast.makeText(this@EnterCodeActivity, res.message, Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    }

    private fun showProgressDialog() {
        progressDialog.setMessage("Please wait...")
        progressDialog.setCancelable(false)
        progressDialog.show()
    }

    private fun dismissProgressDailog() {
        progressDialog.dismiss()
    }


    override fun onResendCode() {
        sendCodeOnEmail()
    }
}