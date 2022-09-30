package com.wiesoftware.spine.ui.home.menus.profile.setting.help.privacy

import android.content.Context
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.wiesoftware.spine.R
import com.wiesoftware.spine.RuntimeLocaleChanger
import com.wiesoftware.spine.data.net.Api
import com.wiesoftware.spine.data.repo.HomeRepositry
import com.wiesoftware.spine.databinding.ActivityPrivacySettingBinding
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance
import java.lang.Exception

class PrivacySettingActivity : AppCompatActivity(),KodeinAware, PrivacySettingEventListener {

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base?.let { RuntimeLocaleChanger.wrapContext(it) })
    }

    override val kodein by kodein()
    val factory: PrivacySettingViewmodelFactory by instance()
    val homeRepositry: HomeRepositry by instance()
    lateinit var binding: ActivityPrivacySettingBinding
    lateinit var userId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_privacy_setting)
        binding=DataBindingUtil.setContentView(this,R.layout.activity_privacy_setting)
        val viewmodel=ViewModelProvider(this,factory).get(PrivacySettingViewmodel::class.java)
        binding.viewmodel=viewmodel
        viewmodel.privacySettingEventListener=this


    }

    override fun onBack() {
        onBackPressed()
    }

    override fun onPrivacyPolicy() {
        openChromeTab(Api.PRIVACY_SPINE)
    }
    private fun openChromeTab(url: String) {
        val uri: Uri = Uri.parse(url)
        val builder = CustomTabsIntent.Builder()

        val params = CustomTabColorSchemeParams.Builder()
            .setNavigationBarColor(ContextCompat.getColor(this, R.color.colorPrimary))
            .setToolbarColor(ContextCompat.getColor(this, R.color.colorPrimary))
            .setSecondaryToolbarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark))
            .build()
        builder.setColorSchemeParams(CustomTabsIntent.COLOR_SCHEME_DARK, params)
        builder.setStartAnimations(
            this,
            android.R.anim.slide_in_left,
            android.R.anim.slide_out_right
        )
        builder.setExitAnimations(
            this,
            android.R.anim.slide_in_left,
            android.R.anim.slide_out_right
        )
        builder.setShowTitle(true)
        if (isChromeInstalled()) {
            builder.build().intent.setPackage("com.android.chrome")
        }
        val customTabsIntent = builder.build()
        customTabsIntent.launchUrl(this, uri)
    }
    private fun isChromeInstalled(): Boolean {
        return try {
            getPackageManager().getPackageInfo("com.android.chrome", 0)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}