package com.wiesoftware.spine

import android.content.Context
import android.content.res.Configuration
import com.wiesoftware.spine.ui.home.menus.profile.setting.account_settings.language.SelectLanguageActivity
import com.wiesoftware.spine.util.Prefs
import com.wiesoftware.spine.util.init
import java.util.*

/**
 * Created by Vivek kumar on 2/11/2021.
 * Email: vivekpcst.kumar@gmail.com.
 */
object RuntimeLocaleChanger {
    fun wrapContext(context: Context): Context {
        init(context)
        val savedLocale = createLocaleFromSavedLanguage() // load the user picked language from persistence (e.g SharedPreferences)
            ?: return context // else return the original untouched context
        // as part of creating a new context that contains the new locale we also need to override the default locale.
        Locale.setDefault(savedLocale)
        // create new configuration with the saved locale
        val newConfig = Configuration()
        newConfig.setLocale(savedLocale)
        return context.createConfigurationContext(newConfig)
    }

    private fun createLocaleFromSavedLanguage(): Locale? {
        val lang= Prefs.getString(SelectLanguageActivity.LANG_ID,"en")
        //val lang= Prefs.getString(SelectLanguageActivity.LANG_ID,"en")
        val langCode=lang!!.toLowerCase(Locale.getDefault())
        return  Locale(langCode)
    }

    fun overrideLocale(context: Context) {
        init(context)
        val savedLocale = createLocaleFromSavedLanguage()
            ?: return
        Locale.setDefault(savedLocale)
        val newConfig = Configuration()
        newConfig.setLocale(savedLocale)
        context.resources.updateConfiguration(newConfig, context.resources.displayMetrics)
        if (context != context.applicationContext) {
            context.applicationContext.resources.run { updateConfiguration(newConfig, displayMetrics) }
        }
    }
}