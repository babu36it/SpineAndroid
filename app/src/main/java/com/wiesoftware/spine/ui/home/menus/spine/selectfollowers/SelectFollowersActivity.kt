package com.wiesoftware.spine.ui.home.menus.spine.selectfollowers

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wiesoftware.spine.R
import com.wiesoftware.spine.RuntimeLocaleChanger
import com.wiesoftware.spine.data.net.reponses.FollowersData
import com.wiesoftware.spine.data.repo.EventRepository
import com.wiesoftware.spine.data.repo.HomeRepository
import com.wiesoftware.spine.databinding.ActivitySelectFollowersBinding
import com.wiesoftware.spine.ui.home.menus.spine.foryou.POST_ID
import com.wiesoftware.spine.ui.home.menus.spine.story.viewstories.storyscreen.StoryDisplayFragment.Companion.IS_STORY
import com.wiesoftware.spine.ui.home.menus.spine.story.viewstories.storyscreen.StoryDisplayFragment.Companion.STORY_ID
import com.wiesoftware.spine.util.ApiException
import com.wiesoftware.spine.util.NoInternetException
import com.wiesoftware.spine.util.toast
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance
import java.lang.Exception


class SelectFollowersActivity : AppCompatActivity(),KodeinAware, SelectFollowersEventListener,
    SelectFollowersAdapter.FollowersEventListener {

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base?.let { RuntimeLocaleChanger.wrapContext(it) })
    }

    override val kodein by kodein()
    val homeRepositry: HomeRepository by instance()
    val eventRepository: EventRepository by instance()
    val factory: SelectFollowersViewmodelFactory by instance()
    lateinit var userId: String
    var selectedData: MutableList<FollowersData> = ArrayList<FollowersData>()
    val data: MutableMap<String,String> = HashMap<String,String>()
    val sData: MutableMap<String,String> = HashMap<String,String>()
    lateinit var binding: ActivitySelectFollowersBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=DataBindingUtil.setContentView(this,R.layout.activity_select_followers)
        val viewmodel=ViewModelProvider(this,factory).get(SelectFollowersViewmodel::class.java)
        binding.viewmodel=viewmodel
        viewmodel.selectFollowersEventListener=this
        viewmodel.getLoggedInUser().observe(this, Observer { user->
            userId=user.users_id!!
            setFollowers()
        })

    }

    private fun setFollowers() {
        lifecycleScope.launch {
            try {
                val followersRes = eventRepository.getFollowers(1, 100, userId)
                if (followersRes.status) {
                    val followersData = followersRes.data
                    binding.rvSelectedFollowers.also {
                        it.layoutManager = GridLayoutManager(this@SelectFollowersActivity,2,RecyclerView.VERTICAL,false)
                            /*LinearLayoutManager(
                            this@SelectFollowersActivity,
                            RecyclerView.VERTICAL,
                            false
                        )*/
                        it.setHasFixedSize(true)
                        it.adapter = SelectFollowersAdapter(followersData, this@SelectFollowersActivity)
                    }
                }
            }catch (e: ApiException){
                e.printStackTrace()
            }catch (e: NoInternetException){
                e.printStackTrace()
            }
        }

    }

    override fun onBack() {
        onBackPressed()
    }

    override fun onNext() {
        try {
         val isStory=intent.getBooleanExtra(IS_STORY,false)
            if(isStory){
                val storyId=intent.getStringExtra(STORY_ID)
                shareStory(storyId)
                return
            }
        }catch (e: Exception){
            e.printStackTrace()
        }

        val post_id=intent.getStringExtra(POST_ID)
        post_id?.let {
            data.put("spine_post_id", it)
            data.put("user_id",userId)
            var i=0
            selectedData.forEach {
                data.put("share_users_id[$i]",it.user_id)
                i++
            }
            lifecycleScope.launch {
              try {
                  val res=homeRepositry.sharePost(data)
                  if (res.status){
                      res.message.toast(this@SelectFollowersActivity)
                      onBackPressed()
                  }else{
                      "${res.message}".toast(this@SelectFollowersActivity)
                  }
              }catch (e: ApiException){
                  e.printStackTrace()
              }catch (e: NoInternetException){
                  e.printStackTrace()
              }
            }
        }
    }

    private fun shareStory(storyId: String?) {
        storyId?.let {
            sData.put("spine_story_id", it)
        sData.put("user_id",userId)
        var i=0
        selectedData.forEach {
            sData.put("share_users_id[$i]",it.user_id)
            i++
        }
        lifecycleScope.launch {
            try {
                val res=homeRepositry.spineStoriesShare(sData)
                if (res.status){
                    res.message.toast(this@SelectFollowersActivity)
                    onBackPressed()
                }else{
                    res.message.toast(this@SelectFollowersActivity)
                }
            }catch (e:  ApiException){
                e.printStackTrace()
            }catch (e: NoInternetException){
                e.printStackTrace()
            }
        }
        }
    }

    override fun followerSelected(followersData: FollowersData) {
        selectedData.add(followersData)
    }
}