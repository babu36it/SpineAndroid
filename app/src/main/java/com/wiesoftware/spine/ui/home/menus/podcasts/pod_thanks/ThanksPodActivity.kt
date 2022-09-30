package com.wiesoftware.spine.ui.home.menus.podcasts.pod_thanks

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.wiesoftware.spine.R
import com.wiesoftware.spine.RuntimeLocaleChanger
import com.wiesoftware.spine.databinding.ActivityThanksPodBinding
import com.wiesoftware.spine.ui.home.HomeActivity
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein

class ThanksPodActivity : AppCompatActivity(),KodeinAware, ThanksPodEventListener {

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base?.let { RuntimeLocaleChanger.wrapContext(it) })
    }

    override val kodein by kodein()
    lateinit var binding: ActivityThanksPodBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=DataBindingUtil.setContentView(this,R.layout.activity_thanks_pod)
        val viewmodel=ViewModelProvider(this).get(ThanksPodViewmodel::class.java)
        binding.viewModel = viewmodel
        viewmodel.thanksPodEventListener = this

    }

    override fun onClose() {
        onBackPressed()
    }

    override fun onClosePodcast() {
        startActivity(Intent(this,HomeActivity::class.java))
        finishAffinity()
    }
}