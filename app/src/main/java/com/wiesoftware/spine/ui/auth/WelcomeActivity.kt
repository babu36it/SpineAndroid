package com.wiesoftware.spine.ui.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil.setContentView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.wiesoftware.spine.R
import com.wiesoftware.spine.data.adapter.WelcomeSliderAdapter
import com.wiesoftware.spine.data.adapter.WelcomeSliderItem
import com.wiesoftware.spine.databinding.ActivityWelcomeBinding
import com.wiesoftware.spine.ui.auth.login.LoginActivity
import com.wiesoftware.spine.ui.auth.register.RegisterActivity
import com.wiesoftware.spine.ui.home.HomeActivity
import org.kodein.di.android.kodein
import org.kodein.di.KodeinAware
import org.kodein.di.generic.instance


class WelcomeActivity : AppCompatActivity(),WelcomeEventListener,KodeinAware {

    override val kodein by kodein()
    private val factory: WelcomeViewModelFactory by instance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityWelcomeBinding= setContentView(this,R.layout.activity_welcome)
        val viewModel=ViewModelProvider(this,factory).get(WelcomeViewModel::class.java)
        binding.viewmodel=viewModel
        viewModel.welcomeEventListener=this

        viewModel.getLoggedInUser().observe(this, Observer {user->
            if (user != null){
                val intent=Intent(this,HomeActivity::class.java)
                intent.flags= Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
        })



        val sliderListItem: MutableList<WelcomeSliderItem> = mutableListOf()
        sliderListItem.add(WelcomeSliderItem(getString(R.string.connect_with_people_with_your_interests),getString(R.string.anytime_anywhere)))
        sliderListItem.add(WelcomeSliderItem(getString(R.string.connect_with_people_with_your_interests),getString(R.string.anytime_anywhere)))
        sliderListItem.add(WelcomeSliderItem(getString(R.string.connect_with_people_with_your_interests),getString(R.string.anytime_anywhere)))
        val sliderAdapter=WelcomeSliderAdapter(this,sliderListItem)
        //binding.imageSlider.setSliderAdapter(sliderAdapter)


    }

    override fun onRegister() {
        startActivity(Intent(this, RegisterActivity::class.java))
    }
    override fun onLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
    }

}