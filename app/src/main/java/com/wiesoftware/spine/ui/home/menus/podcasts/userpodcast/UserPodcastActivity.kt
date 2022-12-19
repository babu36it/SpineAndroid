package com.wiesoftware.spine.ui.home.menus.podcasts.userpodcast

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.wiesoftware.spine.R
import com.wiesoftware.spine.RuntimeLocaleChanger
import com.wiesoftware.spine.data.adapter.UserPodcastAdapter
import com.wiesoftware.spine.data.net.reponses.PodDatas
import com.wiesoftware.spine.data.repo.PodcastRepository
import com.wiesoftware.spine.databinding.ActivityUserPodcastBinding
import com.wiesoftware.spine.ui.home.menus.podcasts.podcastdetails.PodcastDetailActivity
import com.wiesoftware.spine.ui.home.menus.podcasts.watch.WatchPodcastsFragment
import com.wiesoftware.spine.ui.home.menus.profile.someonesprofile.SomeOneProfileActivity
import com.wiesoftware.spine.ui.home.menus.spine.foryou.BASE_IMAGE
import com.wiesoftware.spine.ui.home.menus.spine.foryou.STORY_IMAGE
import com.wiesoftware.spine.util.ApiException
import com.wiesoftware.spine.util.NoInternetException
import com.wiesoftware.spine.util.POD_FILE_BASE
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class UserPodcastActivity : AppCompatActivity(),KodeinAware, UserPodcastEventListener,
    UserPodcastAdapter.OnPodEveListener {

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base?.let { RuntimeLocaleChanger.wrapContext(it) })
    }

    override val kodein by kodein()
    val factory: UserPodcastViewModelFactory by instance()
    val podcastRepository: PodcastRepository by instance()
    lateinit var binding: ActivityUserPodcastBinding
    lateinit var userId: String
    var podUserId=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_user_podcast)
        binding=DataBindingUtil.setContentView(this,R.layout.activity_user_podcast)
        val viewmodel=ViewModelProvider(this,factory).get(UserPodcastViewModel::class.java)
        binding.viewmodel=viewmodel
        viewmodel.userPodcastEventListener=this

        viewmodel.getLoggedInUser().observe(this, Observer { user->
            userId=user.users_id!!
            //getUserPodcast(userId)
        })
        podUserId=intent.getStringExtra(WatchPodcastsFragment.POD_USER_ID)!!
        getUserPodcast(podUserId)
    }

    private fun getUserPodcast(podUserId: String?) {
        lifecycleScope.launch {
            try {
                val res=podcastRepository.getAllPodcasts()
                if (res.status){
                    STORY_IMAGE =res.profile_img
                    POD_FILE_BASE =res.image
                    BASE_IMAGE = POD_FILE_BASE
                    val dataList=res.data
                    setPodData(dataList)
                }
            }catch (e: ApiException){
                e.printStackTrace()
            }catch (e: NoInternetException){
                e.printStackTrace()
            }
        }
    }

    private fun setPodData(dataList: List<PodDatas>) {
        if(dataList.size > 0){
            val podData: PodDatas=dataList[0]
            setUsersData(podData)
        }
        binding.rvUserPod.also {
            it.layoutManager=LinearLayoutManager(this,RecyclerView.VERTICAL,false)
            it.setHasFixedSize(true)
            it.adapter=UserPodcastAdapter(dataList,this)
        }

    }

    private fun setUsersData(podData: PodDatas) {
        binding.textView78.text=podData.user_display_name ?: podData.username
        binding.textView219.text=podData.bio
        Glide.with(binding.imageView27).load(STORY_IMAGE+podData.profile_pic).error(R.drawable.ic_profile).into(binding.imageView27)
        Glide.with(binding.imageView28).load(STORY_IMAGE+podData.profile_pic).error(R.drawable.ic_profile).into(binding.imageView28)
        binding.imageView28.setOnClickListener {
            val intent=Intent(this,SomeOneProfileActivity::class.java)
            intent.putExtra(SomeOneProfileActivity.SOME_ONES_USER_ID,podUserId)
            startActivity(intent)
        }
    }

    override fun onBack() {
        onBackPressed()
    }

    override fun onPodDetails(podcastData: PodDatas) {
        val intent=Intent(this, PodcastDetailActivity::class.java)
        intent.putExtra(WatchPodcastsFragment.POD_ID,podcastData.id)
        startActivity(intent)
    }
}