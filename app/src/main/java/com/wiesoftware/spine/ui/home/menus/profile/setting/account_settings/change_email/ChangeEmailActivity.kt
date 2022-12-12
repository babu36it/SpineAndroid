package com.wiesoftware.spine.ui.home.menus.profile.setting.account_settings.change_email

import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.view.Window
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.wiesoftware.spine.R
import com.wiesoftware.spine.RuntimeLocaleChanger
import com.wiesoftware.spine.data.db.entities.User
import com.wiesoftware.spine.data.repo.HomeRepository
import com.wiesoftware.spine.databinding.ActivityChangeEmailBinding
import com.wiesoftware.spine.ui.auth.WelcomeActivity
import com.wiesoftware.spine.util.ApiException
import com.wiesoftware.spine.util.NoInternetException
import com.wiesoftware.spine.util.hideKeyboard
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class ChangeEmailActivity : AppCompatActivity(), KodeinAware, ChangeEmailEventListener {

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base?.let { RuntimeLocaleChanger.wrapContext(it) })
    }

    override val kodein by kodein()
    lateinit var binding: ActivityChangeEmailBinding
    lateinit var userId: String
    val homeRepositry: HomeRepository by instance()
    val factory: ChangeEmailViewmodelFactory by instance()
    var isLogin = true
    lateinit var user: User
    lateinit var progressDialog: ProgressDialog


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_change_email)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_change_email)
        val viewmodel = ViewModelProvider(this, factory).get(ChangeEmailViewmodel::class.java)
        binding.viewmodel = viewmodel
        viewmodel.changeEmailEventListener = this
        progressDialog = ProgressDialog(this)
        viewmodel.getLoggedInUser().observe(this, Observer { user ->
            if (isLogin) {
                this.user = user
                userId = user.users_id!!
            }

        })
        setActionOnEditText()

    }

    private fun setActionOnEditText() {
        binding.etEmail.imeOptions = EditorInfo.IME_ACTION_DONE
        binding.etEmail.setOnEditorActionListener(
            object : TextView.OnEditorActionListener {
                override fun onEditorAction(
                    v: TextView?,
                    actionId: Int,
                    event: KeyEvent?
                ): Boolean {
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        changePassword()
                        return true;
                    }
                    return false;
                }
            })
    }

    private fun changePassword() {
        val email: String = binding.etEmail.text.toString()
        hideKeyboard()
        if (email.isBlank()) {
            invalidEmailDialog()
        } else {
            changeEmail(email)
        }
    }

    private fun changeEmail(email: String) {
        lifecycleScope.launch {
            try {
                showProgressDialog()
                val res = homeRepositry.requestToChangeEmail(email)
                if (res.status){
                    dismissProgressDailog()
                    emailChangedDialog(res.message)
                }else{
                    dismissProgressDailog()
                    Toast.makeText(this@ChangeEmailActivity,res.message,Toast.LENGTH_SHORT).show()
                }

            } catch (e: ApiException) {
                dismissProgressDailog()
                e.printStackTrace()
            } catch (e: NoInternetException) {
                dismissProgressDailog()
                e.printStackTrace()
            }
        }
    }

    fun invalidEmailDialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.getWindow()?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.invalid_email)
        val ok = dialog.findViewById(R.id.button24) as TextView
        ok.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }

    fun emailChangedDialog(message: String) {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.getWindow()?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.email_changed_dialog)
        val ok = dialog.findViewById(R.id.button24) as TextView
        val textView180=dialog.findViewById(R.id.textView180) as TextView
        textView180.text = message
        ok.setOnClickListener {
            dialog.dismiss()
            lifecycleScope.launch {
                try {
                    homeRepositry.logoutUser(user)
                    isLogin = false
                    val intent = Intent(this@ChangeEmailActivity, WelcomeActivity::class.java)
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                } catch (e: Exception) {

                }
            }

        }
        dialog.show()
    }

    override fun onBack() {
        onBackPressed()
    }

    private fun showProgressDialog() {
        progressDialog.setMessage("Please wait...")
        progressDialog.setCancelable(false)
        progressDialog.show()
    }

    private fun dismissProgressDailog() {
        progressDialog.dismiss()
    }
}