package com.wiesoftware.spine.ui.home.menus.podcasts.reviewpodcast

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.wiesoftware.spine.R
import com.wiesoftware.spine.RuntimeLocaleChanger
import com.wiesoftware.spine.data.adapter.RssItemAdapter
import com.wiesoftware.spine.data.net.reponses.ReviewPodData
import com.wiesoftware.spine.data.net.reponses.RssItem
import com.wiesoftware.spine.data.net.reponses.RssResponse
import com.wiesoftware.spine.data.repo.HomeRepositry
import com.wiesoftware.spine.data.repo.RssRepository
import com.wiesoftware.spine.databinding.ActivityReviewPodcastBinding
import com.wiesoftware.spine.ui.home.menus.podcasts.addpodcasts.AddPodcastActivity
import com.wiesoftware.spine.ui.home.menus.podcasts.addrss.AddRssActivity
import com.wiesoftware.spine.ui.home.menus.podcasts.pod_thanks.ThanksPodActivity
import com.wiesoftware.spine.util.Prefs
import com.wiesoftware.spine.util.toast
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class ReviewPodcastActivity : AppCompatActivity(), KodeinAware, ReviewPodcastEventListener {

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base?.let { RuntimeLocaleChanger.wrapContext(it) })
    }

    override val kodein by kodein()
    lateinit var binding: ActivityReviewPodcastBinding
    val rssRepository: RssRepository by instance()
    val homeRepositry: HomeRepositry by instance()
    var items: List<RssItem> = arrayListOf()
    lateinit var userId: String
    var rssLink = ""
    var image = ""
    var title = ""
    var mediafile = ""
    var description = ""
    var allowcomment = ""
    var category = ""
    var subcategory = ""
    var language = ""
    lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_review_podcast)
        val viewmodel = ViewModelProvider(this).get(ReviewPodcastViewmodel::class.java)
        binding.viewmodel = viewmodel
        viewmodel.reviewPodcastEventListener = this
        progressDialog = ProgressDialog(this)
        getRssFeedByLink()
        getReviewPodData()

    }

    private fun getReviewPodData() {
        val reviewPodData =
            intent.getSerializableExtra(AddPodcastActivity.REVIEW_POD_DATA) as ReviewPodData
        userId = reviewPodData.userId
        category = reviewPodData.category
        subcategory = reviewPodData.subcategory
        allowcomment = reviewPodData.allowcomment
        language = reviewPodData.language

        subcategory = subcategory.dropLast(1)
        //"$subcategory".toast(this)
        //  getUserDetails()
    }

    private fun getUserDetails() {
        lifecycleScope.launch {
            try {
                val res = homeRepositry.getUserDetails(userId)
                if (res.status) {
                    val baseImg = res.image
                    val data = res.data
                    val profilePic = data.profile_pic
                    Glide.with(binding.imageView28)
                        .load(baseImg + profilePic)
                        .placeholder(R.drawable.ic_profile)
                        .error(R.drawable.ic_profile)
                        .into(binding.imageView28)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun getRssFeedByLink() {
        rssLink = Prefs.getString(AddRssActivity.RSS_LINK, "").toString()

        lifecycleScope.launch {
            try {
                showProgressDialog()
                val rssRes = rssRepository.getRssFeedData(rssLink)
                if (rssRes.status) {
                    dismissProgressDailog()

                    setAuthorDetails(rssRes)
                }
            } catch (e: Exception) {
                dismissProgressDailog()
                e.printStackTrace()
            }
        }
    }

    private fun setAuthorDetails(rssRes: RssResponse) {
        val feed = rssRes.data.feed
        image = feed.image
        title = feed.title
        description = feed.description
        Glide.with(binding.podImg)
            .load(image)
            .placeholder(R.drawable.demo)
            .into(binding.podImg)
        binding.tvAutherName.text = feed.author
        binding.tvPodTitle.text = title
        mediafile=rssRes.data.feed.link
        items = rssRes.data.items
        Log.e("RssItems", "=" + rssRes.data.items);
        val episodes = "${items.size} Episodes"
        binding.tvEpisodes.text = episodes


        val adapter = RssItemAdapter(items)
        binding.rvUserPod.also {
            it.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
            it.isNestedScrollingEnabled = false
            it.setHasFixedSize(true)
            it.adapter = adapter
        }

    }

    override fun onBack() {
        onBackPressed()
    }

    override fun onSubmitPodcast() {
        lifecycleScope.launch {
            try {
                showProgressDialog()
                val res = homeRepositry.addRssfeedPodcast(
                    title,
                    description,
                    language,
                    category,
                    subcategory,
                    allowcomment,
                    rssLink,
                    mediafile,
                    image,
                )
                if (res.status) {
                    dismissProgressDailog()
                    "${res.message}".toast(this@ReviewPodcastActivity)
                    startActivity(Intent(this@ReviewPodcastActivity, ThanksPodActivity::class.java))
                }else{
                    dismissProgressDailog()
                    "${res.message}".toast(this@ReviewPodcastActivity)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                dismissProgressDailog()
            }
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