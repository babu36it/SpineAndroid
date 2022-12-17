package com.wiesoftware.spine.ui.auth.welcome


import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle

import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.viewpager.widget.ViewPager
import com.wiesoftware.spine.R
import com.wiesoftware.spine.data.net.reponses.welcompageresponse.WelcomePageReponse
import com.wiesoftware.spine.data.repo.AuthRepository
import com.wiesoftware.spine.data.repo.HomeRepository
import com.wiesoftware.spine.databinding.ActivityWelcomScreenBinding
import com.wiesoftware.spine.ui.auth.WelcomeActivity
import com.wiesoftware.spine.ui.auth.WelcomeEventListener
import com.wiesoftware.spine.ui.auth.WelcomeViewModel
import com.wiesoftware.spine.ui.auth.WelcomeViewModelFactory
import com.wiesoftware.spine.ui.auth.register.RegisterActivity
import com.wiesoftware.spine.ui.home.HomeActivity
import kotlinx.android.synthetic.main.activity_welcom_screen.*
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance


class WelcomScreenActivity : AppCompatActivity(), KodeinAware, WelcomeEventListener {
    override val kodein by kodein()
    private lateinit var pageAdapter: PageAdapter
    private lateinit var pages: ArrayList<WelcomePageReponse.Data>
    private val authRepository: AuthRepository by instance()
    private val factory: WelcomeViewModelFactory by instance()

    private var currentPos = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding: ActivityWelcomScreenBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_welcom_screen)
        val viewmodal = ViewModelProvider(this, factory).get(WelcomeViewModel::class.java)
        binding.viewmodal = viewmodal
        viewmodal.welcomeEventListener = this


        // setContentView(R.layout.activity_welcom_screen)
        viewmodal.getLoggedInUser().observe(this, Observer { user ->
            if (user != null) {
                val intent = Intent(this, HomeActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
        })

        if (Build.VERSION.SDK_INT >= 21) {
            window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        }


        getWelcomeData()

        changeStatusBarColor();

        btnRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        btnLogin.setOnClickListener {
            startActivity(Intent(this, com.wiesoftware.spine.ui.auth.login.LoginActivity::class.java))
        }

//        startActivity(Intent(this, com.wiesoftware.spine.ui.auth.login.LoginActivity::class.java))

    }

    private fun launchHomeScreen() {
        startActivity(Intent(this@WelcomScreenActivity, WelcomeActivity::class.java))
        finishAffinity()
    }

    private var viewPagerPageChangeListener: ViewPager.OnPageChangeListener =
        object : ViewPager.OnPageChangeListener {
            override fun onPageSelected(position: Int) {
                currentPos = position
                if (position == pages.size) {
                    btnRegister.visibility = View.VISIBLE
                    btnLogin.visibility = View.VISIBLE
                } else {
                    btnRegister.visibility = View.GONE
                    btnLogin.visibility = View.GONE
                }
            }

            override fun onPageScrolled(arg0: Int, arg1: Float, arg2: Int) {}
            override fun onPageScrollStateChanged(arg0: Int) {}
        }


    private fun changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window: Window = window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.setStatusBarColor(Color.TRANSPARENT)
        }
    }


    private fun setUpPager(pages: List<WelcomePageReponse.Data>, baseUrl: String) {
        pageAdapter = PageAdapter(pages, baseUrl)
        view_pagers.adapter = pageAdapter;
        view_pagers.currentItem = currentPos
        dots_indicator.setViewPager(view_pagers)
        view_pagers.addOnPageChangeListener(viewPagerPageChangeListener);

    }

    private fun getWelcomeData() {
        pages = ArrayList()
        lifecycleScope.launch {
            try {
                val response = authRepository.getWelcomePages()
                if (response.status) {
                    pages.addAll(response.data)
                    setUpPager(response.data, response.imgURL)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onRegister() {
    }

    override fun onLogin() {
    }
}