package com.wiesoftware.spine.ui.home.menus.profile.setting.account_settings.deactiveaccount

import android.app.AlertDialog
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import android.widget.Button
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.wiesoftware.spine.R
import com.wiesoftware.spine.RuntimeLocaleChanger
import com.wiesoftware.spine.data.db.entities.User
import com.wiesoftware.spine.data.repo.HomeRepositry
import com.wiesoftware.spine.databinding.ActivityDeactivateBinding
import com.wiesoftware.spine.ui.auth.WelcomeActivity
import com.wiesoftware.spine.ui.home.menus.profile.setting.account_settings.language.SelectLanguageActivity
import com.wiesoftware.spine.util.ApiException
import com.wiesoftware.spine.util.NoInternetException
import com.wiesoftware.spine.util.toast
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance
import java.lang.Exception

class DeactivateActivity : AppCompatActivity(),KodeinAware, DeactivateEventListener {

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base?.let { RuntimeLocaleChanger.wrapContext(it) })
    }

    override val kodein by kodein()
    val  factory: DeactivateViewmodelFactory by instance()
    val homeRepositry: HomeRepositry by instance()
    lateinit var binding: ActivityDeactivateBinding
    lateinit var userId: String
    lateinit var user: User
    var isLogin=true
    lateinit var progressDialog: ProgressDialog


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_select_currency)
        binding=DataBindingUtil.setContentView(this,R.layout.activity_deactivate)
        val viewmodel=ViewModelProvider(this,factory).get(DeactivateViewmodel::class.java)
        binding.viewmodel=viewmodel
        viewmodel.deactivateEventListener=this
        progressDialog=ProgressDialog(this)
        viewmodel.getloggedInUser().observe(this, Observer { user->
            if (isLogin){
                this.user=user
                userId=user.users_id!!
            }

        })

        binding.btnDelete.setOnClickListener {
            chooseDealete()
        }

        binding.button80.setOnClickListener {
            chooseDeactivae()
        }
    }

    override fun onBack() {
        onBackPressed()
    }

    override fun onDeactivateAccount() {

    }

    override fun onDeleteAccount() {

    }


    private fun chooseDealete() {
        val builder = AlertDialog.Builder(this)
        //set title for alert dialog
        builder.setTitle("Delete Account")
        //set message for alert dialog
        builder.setMessage("Are You Sure Want to Delete Account")
        builder.setIcon(android.R.drawable.ic_dialog_alert)
       /* val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(true)*/

        builder.setPositiveButton("Yes"){dialogInterface, which ->

              delete()
             // alertDialog.dismiss()

        }
        //performing cancel action
        builder.setNeutralButton("Cancel"){dialogInterface , which ->
        }
        //performing negative action


        // Create the AlertDialog

        builder.show()

    }
    private fun chooseDeactivae() {
        val builder = AlertDialog.Builder(this)
        //set title for alert dialog
        builder.setTitle("Deactivate Account")
        //set message for alert dialog
         builder.setMessage("Are You Sure Want to Deactivate Account")
        builder.setIcon(android.R.drawable.ic_dialog_alert)

   //     val alertDialog: AlertDialog = builder.create()

        builder.setPositiveButton("Yes"){dialogInterface, which ->

            deactivate()


        }
        //performing cancel action
        builder.setNeutralButton("Cancel"){dialogInterface , which ->
        }
        //performing negative action

        builder.show()

        // Create the AlertDialog
     //   alertDialog.setCancelable(true)
     //   alertDialog.show()

    }

    fun delete(){
        lifecycleScope.launch {
            try {
                showProgressDialog()
                val res=homeRepositry.deleteAccount()
                if (res.status){
                    dismissProgressDailog()
                    Toast.makeText(this@DeactivateActivity,res.message,Toast.LENGTH_SHORT).show()

                    homeRepositry.logoutUser(user)
                    isLogin=false
                    val intent=Intent(this@DeactivateActivity,WelcomeActivity::class.java)
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                }else{
                    dismissProgressDailog()
                    Toast.makeText(this@DeactivateActivity,res.message,Toast.LENGTH_SHORT).show()
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

    fun deactivate(){
        lifecycleScope.launch {
            try {
                showProgressDialog()
                val res=homeRepositry.deactivateAccount()
                if (res.status){
                    dismissProgressDailog()
                    Toast.makeText(this@DeactivateActivity,res.message,Toast.LENGTH_SHORT).show()
                    homeRepositry.logoutUser(user)
                    isLogin=false
                    val intent=Intent(this@DeactivateActivity,WelcomeActivity::class.java)
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                }else{
                    dismissProgressDailog()
                    Toast.makeText(this@DeactivateActivity,res.message,Toast.LENGTH_SHORT).show()
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

    private fun showProgressDialog() {
        progressDialog.setMessage("Please wait...")
        progressDialog.setCancelable(false)
        progressDialog.show()
    }

    private fun dismissProgressDailog() {
        progressDialog.dismiss()
    }
}