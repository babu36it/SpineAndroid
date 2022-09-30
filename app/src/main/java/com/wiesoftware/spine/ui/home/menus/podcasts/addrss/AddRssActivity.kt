package com.wiesoftware.spine.ui.home.menus.podcasts.addrss

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.wiesoftware.spine.R
import com.wiesoftware.spine.RuntimeLocaleChanger
import com.wiesoftware.spine.data.repo.HomeRepositry
import com.wiesoftware.spine.data.repo.RssRepository
import com.wiesoftware.spine.databinding.ActivityAddRssBinding
import com.wiesoftware.spine.ui.home.menus.podcasts.addrss.entercode.EnterCodeActivity
import com.wiesoftware.spine.util.Prefs
import com.wiesoftware.spine.util.putAny
import com.wiesoftware.spine.util.toast
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class AddRssActivity : AppCompatActivity(),KodeinAware, AddRssEventListener {

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base?.let { RuntimeLocaleChanger.wrapContext(it) })
    }

    override val kodein by kodein()
    lateinit var binding: ActivityAddRssBinding
    val rssRepository: RssRepository by instance()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=DataBindingUtil.setContentView(this,R.layout.activity_add_rss)
        val viewmodel=ViewModelProvider(this).get(AddRssViewmodel::class.java)
        binding.viewmodel=viewmodel
        viewmodel.addRssEventListener=this

    }

    override fun onBack() {
        onBackPressed()
    }

    override fun validateRss(rssLink: String) {
        getPodcastsformUrl(rssLink)

    }

    override fun onCloseInfo() {
        binding.textView290.visibility = View.GONE
        binding.imageButton78.visibility = View.GONE
    }

    private fun getPodcastsformUrl(rssLink: String) {
        lifecycleScope.launch {
            try {
               val rssRes = rssRepository.getPodsFromRss(rssLink)
                if (rssRes.status.equals("ok")){
                    Log.e("rssRes:",""+rssRes)
                    if (!rssRes.feed.author.isNullOrEmpty()){
                        "Link is successfully validated.".toast(this@AddRssActivity)
                        Prefs.putAny(RSS_LINK,rssLink)
                        startActivity(Intent(this@AddRssActivity,EnterCodeActivity::class.java))
                    }else{
                        binding.textView290.visibility = View.VISIBLE
                        binding.imageButton78.visibility = View.VISIBLE
                        "Invalid link".toast(this@AddRssActivity)
                    }
                }else{
                    binding.textView290.visibility = View.VISIBLE
                    binding.imageButton78.visibility = View.VISIBLE
                    "Validation failed.".toast(this@AddRssActivity)
                }
            }catch (e: Exception){
                e.printStackTrace()
                binding.textView290.visibility = View.VISIBLE
                binding.imageButton78.visibility = View.VISIBLE
                Log.e("rss::",""+e.cause+","+e.message+","+e.printStackTrace())
                "Validation failed.".toast(this@AddRssActivity)
            }
        }
    }

    companion object{
        const val RSS_LINK = "rssLink"
    }
}