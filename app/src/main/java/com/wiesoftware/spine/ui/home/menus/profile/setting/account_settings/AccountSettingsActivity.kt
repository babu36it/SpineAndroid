package com.wiesoftware.spine.ui.home.menus.profile.setting.account_settings

import android.app.Dialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.wiesoftware.spine.R
import com.wiesoftware.spine.RuntimeLocaleChanger
import com.wiesoftware.spine.data.db.entities.User
import com.wiesoftware.spine.data.repo.HomeRepository
import com.wiesoftware.spine.databinding.ActivityAccountSettingsBinding
import com.wiesoftware.spine.ui.auth.WelcomeActivity
import com.wiesoftware.spine.ui.home.menus.profile.setting.account_settings.change_email.ChangeEmailActivity
import com.wiesoftware.spine.ui.home.menus.profile.setting.account_settings.deactiveaccount.DeactivateActivity
import com.wiesoftware.spine.ui.home.menus.profile.setting.account_settings.deleteAccount.DeleteAccouontActivity
import com.wiesoftware.spine.ui.home.menus.profile.setting.account_settings.language.SelectLanguageActivity
import com.wiesoftware.spine.ui.home.menus.profile.setting.account_settings.messaging.MessagingActivity
import com.wiesoftware.spine.ui.home.menus.profile.setting.account_settings.saveEventCalendar.SaveEventToCalActivity
import com.wiesoftware.spine.ui.home.menus.profile.setting.currency.CurrencyActivity
import com.wiesoftware.spine.util.*
import kotlinx.android.synthetic.main.change_password.*
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class AccountSettingsActivity : AppCompatActivity(),KodeinAware, AccountSettingEventListener {

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base?.let { RuntimeLocaleChanger.wrapContext(it) })
    }

    override val kodein by kodein()
    val factory: AccountSettingViewModelFactory by instance()
    val homeRepositry: HomeRepository by instance()
    lateinit var binding: ActivityAccountSettingsBinding
    var userId: String=""
    lateinit var c_user: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_account_settings)
        binding=DataBindingUtil.setContentView(this,R.layout.activity_account_settings)
        val viewmodel=ViewModelProvider(this,factory).get(AccountSettingViewModel::class.java)
        binding.viewmodel=viewmodel
        viewmodel.accountSettingEventListener=this
        viewmodel.getLoggedInUser().observe(this, Observer { user->
            c_user=user
            userId=user.users_id!!
        })
        val isDarkEnable= Prefs.getBoolean(AccountSettingsActivity.IS_DARK_ENABLED,false)
        if (isDarkEnable){
            binding.switch1.isChecked=true
        }

    }

    override fun onBack() {
        onBackPressed()
    }

    override fun onChangePassword() {
        openChngPwdDialog()
    }

    override fun onResume() {
        super.onResume()
        val currencySymbol= Prefs.getString(CurrencyActivity.CURRENCY_NAME,"USD")
        binding.textView48.text=currencySymbol
        val CAL_STATUS= Prefs.getString(SaveEventToCalActivity.CAL_STATUS,"Off")
        binding.textView46.text=CAL_STATUS
        //val langName= Prefs.getString(LANG_NAME,"English")
       // binding.textView47.text=langName
    }

    fun openChngPwdDialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.getWindow()?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.change_password)

        val save = dialog.findViewById(R.id.button66) as Button
        val cancel = dialog.findViewById(R.id.button65) as Button
        save.setOnClickListener {
            val oldPassword:String=dialog.editTextTextPassword3.text.toString()
            val newPassword:String=dialog.editTextTextPassword4.text.toString()
            val confirmPassword:String=dialog.editTextTextPassword5.text.toString()
            if (oldPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()){
                dialog.dismiss()
                return@setOnClickListener
            }
            if (newPassword.equals(confirmPassword)) {
                saveChangedPassword(oldPassword, newPassword)
            }else{
                "Password didn't matched.".toast(this)
            }
            dialog.dismiss()
        }
        cancel.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }

    private fun saveChangedPassword(oldPassword: String, newPassword: String) {
        lifecycleScope.launch {
            try {
                val res=homeRepositry.changePassword(userId,oldPassword,newPassword)
                if (res.status){
                 "Your password has been successfully changed".toast(this@AccountSettingsActivity)
                }else{
                    "Oops! Something went wrong. ".toast(this@AccountSettingsActivity)
                }
            }catch (e: ApiException){
                e.printStackTrace()
            }catch (e: NoInternetException){
                e.printStackTrace()
            }
        }
    }


    override fun onDeleteAccount() {
       startActivity(Intent(this,DeleteAccouontActivity::class.java))
    }

    override fun onChangeEmail() {
        startActivity(Intent(this,ChangeEmailActivity::class.java))
    }

    override fun onMessaging() {
        startActivity(Intent(this,MessagingActivity::class.java))
    }

    override fun onSaveCalendarEvent() {
        startActivity(Intent(this,SaveEventToCalActivity::class.java))
    }

    override fun onLanguageSelect() {
        chooseLanguage()
    }

    override fun onCurrency() {
        startActivity(Intent(this,CurrencyActivity::class.java))
    }

    override fun onDarkMode(isChecked: Boolean) {
        if(isChecked){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            Prefs.putAny(IS_DARK_ENABLED,true)
            startActivity(Intent(this,WelcomeActivity::class.java))
            finishAffinity()

        }else{
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            Prefs.putAny(IS_DARK_ENABLED,false)
            startActivity(Intent(this,WelcomeActivity::class.java))
            finishAffinity()
        }
    }

    override fun onDeactivateAccount() {
        startActivity(Intent(this,DeactivateActivity::class.java))
    }






    private fun chooseLanguage() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.getWindow()?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.language_confirmation_dialog)
        val continueBtn = dialog.findViewById(R.id.button71) as TextView
        val cancel = dialog.findViewById(R.id.button25) as TextView
        continueBtn.setOnClickListener {
            startActivity(Intent(this,SelectLanguageActivity::class.java))
            dialog.dismiss()
        }
        cancel.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }

    companion object{
        const val IS_DARK_ENABLED: String= "isDarkModeEnabled"
    }
}