package com.wiesoftware.spine.ui.home.menus.spine.featuredpost.thankyou_featured

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.wiesoftware.spine.R
import com.wiesoftware.spine.RuntimeLocaleChanger
import com.wiesoftware.spine.databinding.ActivityThanksFeaturedBinding
import com.wiesoftware.spine.ui.home.HomeActivity
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein

class ThanksFeaturedActivity : AppCompatActivity(), KodeinAware, ThankYouFeaturedEventListener {

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base?.let { RuntimeLocaleChanger.wrapContext(it) })
    }

    override val kodein by kodein()
    lateinit var binding: ActivityThanksFeaturedBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_thanks_featured)
        val viewmodel = ViewModelProvider(this).get(ThankYouFeaturedViewmodel::class.java)
        binding.viewmodel = viewmodel
        viewmodel.thankYouFeaturedEventListener = this
    }

    override fun onClose() {
        startActivity(Intent(this, HomeActivity::class.java))
    }


}