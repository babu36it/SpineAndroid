package com.wiesoftware.spine.ui.home.menus.profile.setting.help

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.wiesoftware.spine.R
import com.wiesoftware.spine.RuntimeLocaleChanger
import com.wiesoftware.spine.data.net.Api
import com.wiesoftware.spine.databinding.ActivityHelpBinding
import com.wiesoftware.spine.ui.home.menus.profile.setting.help.privacy.PrivacySettingActivity
import com.wiesoftware.spine.util.GetAppVersion
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance
import java.lang.Exception

class HelpActivity : AppCompatActivity(),KodeinAware, HelpEventListener {

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base?.let { RuntimeLocaleChanger.wrapContext(it) })
    }

    override val kodein by kodein()
    val factory: HelpViewModelFactory by instance()
    lateinit var binding: ActivityHelpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_help)
        binding=DataBindingUtil.setContentView(this, R.layout.activity_help)
        val viewmodel=ViewModelProvider(this, factory).get(HelpViewModel::class.java)
        binding.viewmodel=viewmodel
        viewmodel.helpEventListener=this
        binding.textView230.text= GetAppVersion(this)
    }

    override fun onBack() {
        onBackPressed()
    }

    override fun onHelp() {
        openChromeTab(Api.HELP_SPINE)
    }

    override fun onGiveFeedback() {
        val intent = Intent(Intent.ACTION_SEND)
        val recipients = arrayOf("vivekpcst.kumar@gmail.com")
        intent.putExtra(Intent.EXTRA_EMAIL, recipients)
        intent.putExtra(Intent.EXTRA_SUBJECT, "Spine Feedback")
        intent.putExtra(Intent.EXTRA_TEXT, "Hi Spine, \n")
        //intent.putExtra(Intent.EXTRA_CC, "mailcc@gmail.com")
        intent.type = "text/html"
        intent.setPackage("com.google.android.gm")
        startActivity(Intent.createChooser(intent, "Send Spine feedback"))
    }

    override fun onCommunityGuidelines() {
        openChromeTab(Api.GUIDELINE_SPINE)
    }

    override fun onAboutSpine() {
        openChromeTab(Api.ABOUT_SPINE)
    }

    override fun onTermsOfService() {
        openChromeTab(Api.TERMS_SPINE)
    }

    override fun onPrivacySettings() {
        startActivity(Intent(this, PrivacySettingActivity::class.java))
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