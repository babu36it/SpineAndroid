package com.wiesoftware.spine.ui.home.menus.profile.setting.account_settings.language

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wiesoftware.spine.R
import com.wiesoftware.spine.RuntimeLocaleChanger
import com.wiesoftware.spine.data.repo.HomeRepository
import com.wiesoftware.spine.databinding.ActivitySelectLanguageBinding
import com.wiesoftware.spine.util.Prefs
import com.wiesoftware.spine.util.putAny
import com.wiesoftware.spine.util.toast
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class SelectLanguageActivity : AppCompatActivity(),KodeinAware, SelectLanguageEventListener,
    LangDataAdapter.LanguageSelectedListener {

    override val kodein by kodein()

    val factory: SelectLanguageViewmodelFactory by instance()
    val homeRepositry: HomeRepository by instance()
    lateinit var userId: String
    lateinit var binding: ActivitySelectLanguageBinding
    var langDataList: MutableList<LanguageData> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_select_language)
        //RuntimeLocaleChanger.overrideLocale(this)
        binding=DataBindingUtil.setContentView(this,R.layout.activity_select_language)
        val viewmodel=ViewModelProvider(this,factory).get(SelectLanguageViewmodel::class.java)
        binding.viewmodel=viewmodel
        viewmodel.selectLanguageEventListener=this
        viewmodel.getLoggedInUser().observe(this, Observer { user->
            userId=user.users_id!!
        })
        initLanguageList()
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base?.let { RuntimeLocaleChanger.wrapContext(it) })
    }


    private fun initLanguageList() {
        langDataList.add(LanguageData("DE","German"))
        langDataList.add(LanguageData("EN","English"))
        langDataList.add(LanguageData("FR","French"))
        langDataList.add(LanguageData("IT","Italian"))
        langDataList.add(LanguageData("JA","Japanese"))
        langDataList.add(LanguageData("ES","Spanish"))
        langDataList.add(LanguageData("NL","Dutch"))
        langDataList.add(LanguageData("PL","Polish"))
        langDataList.add(LanguageData("PT","Portuguese"))
        langDataList.add(LanguageData("RU","Russian"))
        langDataList.add(LanguageData("ZH","Chinese"))
        binding.rvLangList.also {
            it.layoutManager=LinearLayoutManager(this,RecyclerView.VERTICAL,false)
            it.setHasFixedSize(true)
            it.adapter=LangDataAdapter(langDataList,this)
        }
    }

    override fun onBack() {
        onBackPressed()
    }
    companion object{
        val LANG_ID="langId"
        val LANG_NAME="langName"
    }

    override fun onLanguageSelected(langData: LanguageData) {
        Prefs.putAny(LANG_ID,langData.langId)
        Prefs.putAny(LANG_NAME,langData.langName)
        "Restart application for better performance.".toast(this)
        RuntimeLocaleChanger.overrideLocale(this)
        recreate()
        //onBackPressed()
    }
}