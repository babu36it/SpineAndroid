package com.wiesoftware.spine.ui.home.menus.profile.setting.help.privacy

import android.app.ProgressDialog
import android.content.Context
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.wiesoftware.spine.R
import com.wiesoftware.spine.RuntimeLocaleChanger
import com.wiesoftware.spine.data.net.Api
import com.wiesoftware.spine.data.repo.HomeRepositry
import com.wiesoftware.spine.databinding.ActivityPrivacySettingBinding
import com.wiesoftware.spine.ui.home.menus.profile.setting.account_settings.saveEventCalendar.SaveEventToCalActivity
import com.wiesoftware.spine.util.ApiException
import com.wiesoftware.spine.util.NoInternetException
import com.wiesoftware.spine.util.Prefs
import com.wiesoftware.spine.util.putAny
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance
import java.lang.Exception

class PrivacySettingActivity : AppCompatActivity(), KodeinAware, PrivacySettingEventListener {

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base?.let { RuntimeLocaleChanger.wrapContext(it) })
    }

    override val kodein by kodein()
    val factory: PrivacySettingViewmodelFactory by instance()
    val homeRepositry: HomeRepositry by instance()
    lateinit var binding: ActivityPrivacySettingBinding
    lateinit var userId: String
    lateinit var progressDialog: ProgressDialog
    private var findabilityStatus: String = "";
    private var personalizedStatus: String = "";
    private var customizationStatus: String = "";

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_privacy_setting)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_privacy_setting)
        val viewmodel = ViewModelProvider(this, factory).get(PrivacySettingViewmodel::class.java)
        binding.viewmodel = viewmodel
        viewmodel.privacySettingEventListener = this
        progressDialog = ProgressDialog(this)

        val findability_status = Prefs.getString(PrivacySettingActivity.findability_Status, "Off")
        if (findability_status.equals("On")) {
            binding.switch8.isChecked = true
        }

        val personalized_status = Prefs.getString(PrivacySettingActivity.personalized_status, "Off")
        if (personalized_status.equals("On")) {
            binding.switch9.isChecked = true
        }

        val customization_status = Prefs.getString(PrivacySettingActivity.customization_status, "Off")
        if (customization_status.equals("On")) {
            binding.switch10.isChecked = true
        }
    }

    override fun onBack() {
        onBackPressed()
    }

    override fun onPrivacyPolicy() {
        openChromeTab(Api.PRIVACY_SPINE)
    }

    override fun findabilityOnchanged(isChecked: Boolean) {
        if (isChecked) {
            findabilityStatus = "1";
        } else {
            findabilityStatus = "0";
        }
        saveFindabilityStatus(findabilityStatus)
    }

    override fun personalizedOnChanged(isChecked: Boolean) {

        if (isChecked) {
            personalizedStatus = "1";
        } else {
            personalizedStatus = "0";
        }
        savePersonalizedStatus(personalizedStatus)

    }

    override fun customizationOnChanged(isChecked: Boolean) {


        if (isChecked) {
            customizationStatus = "1";
        } else {
            customizationStatus = "0";
        }
        saveCustomizationStatus(customizationStatus)
    }



    override fun necessaryOnChanged(isChecked: Boolean) {
    }


    private fun saveFindabilityStatus(status: String) {
        lifecycleScope.launch {
            try {
                showProgressDialog()
                val res = homeRepositry.requestToPrivarcySetting(status, "", "", "")
                if (res.status) {
                    dismissProgressDailog()
                    Toast.makeText(this@PrivacySettingActivity, res.message, Toast.LENGTH_SHORT)
                        .show()
                    val findabilityStatus = if (status == "0") {
                        "Off"
                    } else {
                        "On"
                    }
                    Prefs.putAny(PrivacySettingActivity.findability_Status, findabilityStatus)
                } else {
                    dismissProgressDailog()
                    Toast.makeText(this@PrivacySettingActivity, res.message, Toast.LENGTH_SHORT)
                        .show()

                }
            } catch (e: ApiException) {
                dismissProgressDailog()
                e.printStackTrace()
            } catch (e: NoInternetException) {
                dismissProgressDailog()
                e.printStackTrace()
            }
        }
    }


    private fun savePersonalizedStatus(status: String) {
        lifecycleScope.launch {
            try {
                showProgressDialog()
                val res = homeRepositry.requestToPrivarcySetting("", "", "", status)
                if (res.status) {
                    dismissProgressDailog()
                    Toast.makeText(this@PrivacySettingActivity, res.message, Toast.LENGTH_SHORT)
                        .show()
                    val personalizedStatus = if (status == "0") {
                        "Off"
                    } else {
                        "On"
                    }
                    Prefs.putAny(PrivacySettingActivity.personalized_status, personalizedStatus)
                } else {
                    dismissProgressDailog()
                    Toast.makeText(this@PrivacySettingActivity, res.message, Toast.LENGTH_SHORT)
                        .show()

                }
            } catch (e: ApiException) {
                dismissProgressDailog()
                e.printStackTrace()
            } catch (e: NoInternetException) {
                dismissProgressDailog()
                e.printStackTrace()
            }
        }
    }

    private fun saveCustomizationStatus(status: String) {
        lifecycleScope.launch {
            try {
                showProgressDialog()
                val res = homeRepositry.requestToPrivarcySetting("", status, "", "")
                if (res.status) {
                    dismissProgressDailog()
                    Toast.makeText(this@PrivacySettingActivity, res.message, Toast.LENGTH_SHORT)
                        .show()
                    val customizationStatus = if (status == "0") {
                        "Off"
                    } else {
                        "On"
                    }
                    Prefs.putAny(PrivacySettingActivity.customization_status, customizationStatus)
                } else {
                    dismissProgressDailog()
                    Toast.makeText(this@PrivacySettingActivity, res.message, Toast.LENGTH_SHORT)
                        .show()

                }
            } catch (e: ApiException) {
                dismissProgressDailog()
                e.printStackTrace()
            } catch (e: NoInternetException) {
                dismissProgressDailog()
                e.printStackTrace()
            }
        }
    }


    companion object {
        val findability_Status = "findabilityStatus"
        val personalized_status = "personalizedStatus"
        val customization_status = "customizationStatus"
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

    private fun showProgressDialog() {
        progressDialog.setMessage("Please wait...")
        progressDialog.setCancelable(false)
        progressDialog.show()
    }

    private fun dismissProgressDailog() {
        progressDialog.dismiss()
    }
}