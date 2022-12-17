package com.wiesoftware.spine.ui.home.menus.profile.setting.account_settings.messaging

import android.app.ProgressDialog
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.RadioGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.wiesoftware.spine.R
import com.wiesoftware.spine.RuntimeLocaleChanger
import com.wiesoftware.spine.data.repo.HomeRepository
import com.wiesoftware.spine.data.repo.SettingsRepository
import com.wiesoftware.spine.databinding.ActivityMessagingBinding
import com.wiesoftware.spine.util.*
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class MessagingActivity : AppCompatActivity(),KodeinAware, MessagingEventListener {

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base?.let { RuntimeLocaleChanger.wrapContext(it) })
    }

    override val kodein by kodein()
    val factory: MessagingViewmodelFactory by  instance()
    val settingsRepository: SettingsRepository by instance()
    lateinit var binding: ActivityMessagingBinding
    lateinit var userId: String
    lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_messaging)
        binding=DataBindingUtil.setContentView(this,R.layout.activity_messaging)
        val viewmodel=ViewModelProvider(this,factory).get(MessagingViewmodel::class.java)
        binding.viewmodel=viewmodel
        viewmodel.messagingEventListener=this
        progressDialog = ProgressDialog(this)
        viewmodel.getLoggedInUser().observe(this, Observer { user->
            userId=user.users_id!!
        })

        val messageAuth= Prefs.getString(MSG_AUTH,"3")
        if (messageAuth.equals("1")){
            binding.radioButton4.isChecked=true
        }else if (messageAuth.equals("2")){
            binding.radioButton5.isChecked=true
        }else{
            binding.radioButton6.isChecked=true
        }
    }

    override fun onBack() {
        onBackPressed()
    }

    override fun onMessagingTypeChanged(rg: RadioGroup, id: Int) {
        val ids=rg.checkedRadioButtonId
        when(ids){
            R.id.radioButton4 ->{
                whoCanMessage("1")
            }
            R.id.radioButton5 ->{
                whoCanMessage("2")
            }
            R.id.radioButton6 ->{
                whoCanMessage("3")
            }
        }
    }

    private fun whoCanMessage(msg_auth:String){
        lifecycleScope.launch {
            try{
                showProgressDialog()
                val res=settingsRepository.whoCanMessage(msg_auth)
                if (res.status){
                    dismissProgressDailog()
                    Toast.makeText(this@MessagingActivity,res.message,Toast.LENGTH_SHORT).show()
                    Prefs.putAny(MSG_AUTH,msg_auth)
                }else{
                    dismissProgressDailog()
                    Toast.makeText(this@MessagingActivity,res.message,Toast.LENGTH_SHORT).show()
                }
            }catch (e: ApiException){
                e.printStackTrace()
                dismissProgressDailog()
            }catch (e: NoInternetException){
                e.printStackTrace()
                dismissProgressDailog()
            }
        }
    }

    companion object{
        val MSG_AUTH="msgAuth"
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