package com.wiesoftware.spine.ui.home.menus.spine.featuredpost

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.wiesoftware.spine.R
import com.wiesoftware.spine.RuntimeLocaleChanger
import com.wiesoftware.spine.databinding.ActivityPayment2Binding
import com.wiesoftware.spine.databinding.ActivityThanksFeaturedBinding
import com.wiesoftware.spine.ui.home.HomeActivity
import com.wiesoftware.spine.ui.home.menus.spine.featuredpost.thankyou_featured.ThankYouFeaturedViewmodel
import com.wiesoftware.spine.ui.home.menus.spine.featuredpost.thankyou_featured.ThanksFeaturedActivity
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein

class PaymentActivity : AppCompatActivity(), KodeinAware, PaymentFeaturedEventListner {


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
    }


    override fun onClose() {
        onBackPressed()
    }

    override fun onNext() {
        startActivity(Intent(this, ThanksFeaturedActivity::class.java))
    }

}