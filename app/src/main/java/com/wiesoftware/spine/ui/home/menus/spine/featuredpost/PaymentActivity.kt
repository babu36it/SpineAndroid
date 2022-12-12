package com.wiesoftware.spine.ui.home.menus.spine.featuredpost

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.wiesoftware.spine.R
import com.wiesoftware.spine.RuntimeLocaleChanger
import com.wiesoftware.spine.data.repo.HomeRepository
import com.wiesoftware.spine.databinding.ActivityPayment2Binding
import com.wiesoftware.spine.ui.home.menus.spine.featuredpost.thankyou_featured.ThanksFeaturedActivity
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class PaymentActivity : AppCompatActivity(), KodeinAware, PaymentFeaturedEventListner {

    lateinit var progressDialog: ProgressDialog
    val homeRepositry: HomeRepository by instance()

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base?.let { RuntimeLocaleChanger.wrapContext(it) })
    }


    lateinit var binding: ActivityPayment2Binding
    override val kodein by kodein()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_payment2)
        val viewmodel = ViewModelProvider(this).get(PaymentFeaturedViewModel::class.java)
        binding.viewmodel = viewmodel
        viewmodel.paymentFeaturedEventListner = this
        progressDialog = ProgressDialog(this)
        setAdTypeData()
        mNetworkCallPaymentSettingsAPI()
    }


    override fun onClose() {
        onBackPressed()
    }

    override fun onNext() {
        startActivity(Intent(this, ThanksFeaturedActivity::class.java))
    }

    private fun setAdTypeData() {
       /* binding.tvAdType.setText()*/
    }

    private fun mNetworkCallPaymentSettingsAPI() {
        lifecycleScope.launch {
            try {
                showProgressDialog()
                val res = homeRepositry.getPaymentSettings()
                if (res.status) {
                    dismissProgressDailog()
                    val clientsecret = res.data.client_secret
                    val secret = res.data.secret_key
                    Toast.makeText(this@PaymentActivity, secret, Toast.LENGTH_SHORT).show()

                } else {
                    dismissProgressDailog()
                    Toast.makeText(this@PaymentActivity, res.message, Toast.LENGTH_SHORT).show()
                }

            } catch (e: Exception) {
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