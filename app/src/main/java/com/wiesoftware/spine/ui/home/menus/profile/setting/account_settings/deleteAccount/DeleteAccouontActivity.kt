package com.wiesoftware.spine.ui.home.menus.profile.setting.account_settings.deleteAccount

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.wiesoftware.spine.R
import com.wiesoftware.spine.RuntimeLocaleChanger
import com.wiesoftware.spine.data.db.entities.User
import com.wiesoftware.spine.data.repo.HomeRepository
import com.wiesoftware.spine.databinding.ActivityDeleteAccouontBinding
import com.wiesoftware.spine.ui.auth.WelcomeActivity
import com.wiesoftware.spine.util.ApiException
import com.wiesoftware.spine.util.NoInternetException
import com.wiesoftware.spine.util.toast
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class DeleteAccouontActivity : AppCompatActivity(),KodeinAware, DeleteAccountEventListener {

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base?.let { RuntimeLocaleChanger.wrapContext(it) })
    }

    override val kodein by kodein()
    val factory: DeleteAccountViewmodelFactory by instance()
    val homeRepositry: HomeRepository by instance()
    lateinit var binding: ActivityDeleteAccouontBinding
    lateinit var user: User
    var isLogin=true
    lateinit var userId: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=DataBindingUtil.setContentView(this,R.layout.activity_delete_accouont)
        val viewmodel=ViewModelProvider(this,factory).get(DeleteAccountViewmodel::class.java)
        binding.viewmodel=viewmodel
        viewmodel.deleteAccountEventListener=this
        viewmodel.getLoggedInUser().observe(this, Observer { user->
            if (isLogin){
                this.user=user
            }
        })

    }

    override fun onBack() {
        onBackPressed()
    }

    override fun onDelete() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.spine_alert))
        builder.setMessage(getString(R.string.delete_account_confirmation))
        builder.setPositiveButton(getString(R.string.yes)) { dialog, which ->
            deleteAccount()
            dialog.dismiss()
        }
        builder.setNegativeButton(R.string.no) { dialog, which ->
            dialog.dismiss()
        }
        builder.show()
    }

    private fun deleteAccount() {
        lifecycleScope.launch {
            try {
                val res=homeRepositry.deleteAccount()
                if (res.status){
                    "You are removed from spine".toast(this@DeleteAccouontActivity)
                     removeFromSystem()
                }else{
                    res.message.toast(this@DeleteAccouontActivity)
                }
            }catch (e: ApiException){
                e.printStackTrace()
            }catch (e: NoInternetException){
                e.printStackTrace()
            }
        }
    }
    private fun removeFromSystem() {
        lifecycleScope.launch {
            try {
                val res= homeRepositry.logoutUser(user)
                Log.e("logout::",""+res)
                isLogin=false
                val intent = Intent(this@DeleteAccouontActivity, WelcomeActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)

            }catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}