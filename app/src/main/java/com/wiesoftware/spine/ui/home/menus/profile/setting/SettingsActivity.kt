package com.wiesoftware.spine.ui.home.menus.profile.setting

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.app.ShareCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.wiesoftware.spine.BuildConfig
import com.wiesoftware.spine.R
import com.wiesoftware.spine.RuntimeLocaleChanger
import com.wiesoftware.spine.data.db.entities.User
import com.wiesoftware.spine.data.repo.HomeRepository
import com.wiesoftware.spine.databinding.ActivitySettingsBinding
import com.wiesoftware.spine.ui.auth.WelcomeActivity
import com.wiesoftware.spine.ui.home.menus.profile.editprofile.EditProfileActivity
import com.wiesoftware.spine.ui.home.menus.profile.setting.account_settings.AccountSettingsActivity
import com.wiesoftware.spine.ui.home.menus.profile.setting.help.HelpActivity
import com.wiesoftware.spine.ui.home.menus.profile.setting.my_ads.MyAdsActivity
import com.wiesoftware.spine.ui.home.menus.profile.setting.notifications.NotificationsActivity
import com.wiesoftware.spine.ui.home.menus.spine.foryou.IS_WELCOME_SEEN
import com.wiesoftware.spine.util.Prefs
import com.wiesoftware.spine.util.putAny
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance


class SettingsActivity : AppCompatActivity(), KodeinAware, SettingsEventListener {

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base?.let { RuntimeLocaleChanger.wrapContext(it) })
    }

    override val kodein by kodein()
    val factory: SettingViewModelFactory by instance()
    val homeRepository: HomeRepository by instance()
    lateinit var viewmodel: SettingsViewModel
    lateinit var binding: ActivitySettingsBinding
    lateinit var c_user: User
    var isLogin=true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=DataBindingUtil.setContentView(this, R.layout.activity_settings)
        viewmodel=ViewModelProvider(this, factory).get(SettingsViewModel::class.java)
        binding.viewmodel=viewmodel
        viewmodel.settingsEventListener=this
        viewmodel.getUser().observe(this, Observer { user ->
            if (isLogin){
                c_user = user
            }
        })

    }

    override fun onBack() {
        onBackPressed()
    }

    override fun onLogout() {

        if (c_user != null) {
            lifecycleScope.launch {
                try {
                   val res= homeRepository.logoutUser(c_user)
                    isLogin=false
                    Prefs.putAny(IS_WELCOME_SEEN,false)
                    Log.e("logout::",""+res)
                    val intent = Intent(this@SettingsActivity, WelcomeActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)

                }catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

    }

    override fun onProfileEdit() {
        startActivity(Intent(this, EditProfileActivity::class.java))
    }


    override fun onInvite() {
        ShareCompat.IntentBuilder.from(this)
            .setType("text/plain")
            .setChooserTitle("Invite friends")
            .setText("Let me recommend you best social network app. You can install it with below link.\n  http://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID)
            .startChooser()
    }

    override fun onNotifications() {
        startActivity(Intent(this,NotificationsActivity::class.java))
    }

    override fun onAccountSettings() {
        startActivity(Intent(this,AccountSettingsActivity::class.java))
    }

    override fun onHelp() {
        startActivity(Intent(this,HelpActivity::class.java))
    }

    override fun onMyAds() {
        startActivity(Intent(this,MyAdsActivity::class.java))
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