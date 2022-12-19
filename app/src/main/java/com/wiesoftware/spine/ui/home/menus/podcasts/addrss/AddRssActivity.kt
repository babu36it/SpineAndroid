package com.wiesoftware.spine.ui.home.menus.podcasts.addrss

import android.app.ProgressDialog
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
import com.wiesoftware.spine.data.repo.PodcastRepository
import com.wiesoftware.spine.databinding.ActivityAddRssBinding
import com.wiesoftware.spine.ui.home.menus.podcasts.addrss.entercode.EnterCodeActivity
import com.wiesoftware.spine.util.Prefs
import com.wiesoftware.spine.util.putAny
import com.wiesoftware.spine.util.toast
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class AddRssActivity : AppCompatActivity(), KodeinAware, AddRssEventListener {

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base?.let { RuntimeLocaleChanger.wrapContext(it) })
    }

    override val kodein by kodein()
    lateinit var binding: ActivityAddRssBinding
    val podcastRepository: PodcastRepository by instance()
    lateinit var progressDialog: ProgressDialog


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_rss)
        val viewmodel = ViewModelProvider(this).get(AddRssViewModel::class.java)
        binding.viewmodel = viewmodel
        viewmodel.addRssEventListener = this
        progressDialog=ProgressDialog(this)

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
                showProgressDialog()
                val rssRes = podcastRepository.getPodsFromRss(rssLink)

                if (rssRes.status) {
                    dismissProgressDailog()
                    Log.e("rssRes:", "" + rssRes)
                    "Link is successfully validated.".toast(this@AddRssActivity)
                    Prefs.putAny(RSS_LINK, rssLink)
                    startActivity(Intent(this@AddRssActivity, EnterCodeActivity::class.java))
                } else {
                    dismissProgressDailog()
                    binding.rrValidationFailed.visibility = View.VISIBLE
                    "Validation failed.".toast(this@AddRssActivity)
                }
            } catch (e: Exception) {
                dismissProgressDailog()
                e.printStackTrace()
                binding.rrValidationFailed.visibility = View.VISIBLE
                Log.e("rss::", "" + e.cause + "," + e.message + "," + e.printStackTrace())
                "Validation failed.".toast(this@AddRssActivity)
            }
        }
    }

    private fun showProgressDialog(){
        progressDialog.setMessage("Please wait...")
        progressDialog.setCancelable(false)
        progressDialog.show()
    }
    private fun dismissProgressDailog(){
       progressDialog.dismiss()
    }

    companion object {
        const val RSS_LINK = "rssLink"
    }
}