package com.wiesoftware.spine.ui.home.menus.spine.story

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wiesoftware.spine.R
import com.wiesoftware.spine.RuntimeLocaleChanger
import com.wiesoftware.spine.data.adapter.StoriesAdapter
import com.wiesoftware.spine.data.adapter.StoryAdapter
import com.wiesoftware.spine.data.net.reponses.AllUsersData
import com.wiesoftware.spine.data.net.reponses.FollwingData
import com.wiesoftware.spine.data.repo.HomeRepository
import com.wiesoftware.spine.databinding.ActivityStoryBinding
import com.wiesoftware.spine.databinding.StoriesFollowingItemBinding
import com.wiesoftware.spine.databinding.StoryItemBinding
import com.wiesoftware.spine.ui.home.menus.profile.someonesprofile.SomeOneProfileActivity
import com.wiesoftware.spine.ui.home.menus.spine.story.viewstories.ViewStoryActivity
import com.wiesoftware.spine.util.ApiException
import com.wiesoftware.spine.util.BaseAdapter
import com.wiesoftware.spine.util.NoInternetException
import com.wiesoftware.spine.util.toast
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class StoryActivity : AppCompatActivity(), KodeinAware, StoryEventListener {

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base?.let { RuntimeLocaleChanger.wrapContext(it) })
    }

    override val kodein by kodein()
    private val factory: StoryViewmodelFactory by instance()
    private val homeRepositry: HomeRepository by instance()

    lateinit var binding: ActivityStoryBinding
    lateinit var userId: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_story)
        val viewmodel = ViewModelProvider(this, factory).get(StoryViewModel::class.java)
        binding.viewmodel = viewmodel
        viewmodel.storyEventListener = this
        viewmodel.getLoggedInUser().observe(this, Observer { user ->
            userId = user.users_id!!
            setStory()
        })

    }

    private fun setStory() {

        lifecycleScope.launch {
            try {
                /*val allUsersRes=homeRepositry.getAllUsers(1,100,userId)   Temp Commented MT
                if (allUsersRes.status){
                    BASE_IMAGE=allUsersRes.image
                    val allUsersData=allUsersRes.data
                    binding.rvStory.also {
                        it.layoutManager=GridLayoutManager(this@StoryActivity,3,RecyclerView.VERTICAL,false)
                        it.setHasFixedSize(true)
                        it.adapter=StoryAdapter(allUsersData,this@StoryActivity)
                    }
                }*/
                var allUsersData: MutableList<AllUsersData> = ArrayList()
                allUsersData.add(AllUsersData("", "", "Sophia", "", "", "", "", "", ""))
                allUsersData.add(AllUsersData("", "", "Json", "", "", "", "", "", ""))
                allUsersData.add(AllUsersData("", "", "Oliver", "", "", "", "", "", ""))
                allUsersData.add(AllUsersData("", "", "Brendon", "", "", "", "", "", ""))
                allUsersData.add(AllUsersData("", "", "Jay", "", "", "", "", "", ""))
                allUsersData.add(AllUsersData("", "", "Colline", "", "", "", "", "", ""))
                allUsersData.add(AllUsersData("", "", "Sophia", "", "", "", "", "", ""))

                var mAdapter = BaseAdapter<AllUsersData>(this@StoryActivity)
                mAdapter!!.listOfItems = allUsersData
                mAdapter!!.expressionViewHolderBinding = { data, viewBinding, context ->
                    var holder = viewBinding as StoryItemBinding
                    holder.modal = data
                    holder.button15.setOnClickListener {
                        holder.button15.text = getString(
                            R.string
                                .following
                        )
                        //listener.
                        onStoryFollowing(data)
                    }
                    holder.textView26.text = data.userName
                    holder.circleImageView2.setOnClickListener {
                        //listener.
                        onViewSomeonesProfile(data)
                    }
                }


                mAdapter!!.expressionOnCreateViewHolder = { viewGroup ->
                    StoryItemBinding.inflate(
                        LayoutInflater.from(viewGroup.context),
                        viewGroup,
                        false
                    )
                }

                binding.rvStory.also {
                    it.layoutManager =
                        GridLayoutManager(this@StoryActivity, 3, RecyclerView.VERTICAL, false)
                    it.setHasFixedSize(true)
                    it.adapter = mAdapter//StoryAdapter(allUsersData,this@StoryActivity)
                }


            } catch (e: ApiException) {
                e.printStackTrace()
            } catch (e: NoInternetException) {
                e.printStackTrace()
            }
        }

        /*  comment for temp MT checking
        lifecycleScope.launch {
               try {
                   val storyRes=homeRepositry.getFollowingUsersStoryList(1, 100, userId)//homeRepositry.getYourStories(userId)
                   if (storyRes.status){
                       STORY_IMAGE =storyRes.image
                       val storyList: List<FollwingData> = storyRes.data
                       binding.rvStory.also {
                           it.layoutManager= GridLayoutManager(this@StoryActivity,3,RecyclerView.VERTICAL,false)
                           it.setHasFixedSize(true)
                           it.adapter= StoriesAdapter(storyList, this@StoryActivity, 1, userId)
                       }
                   }

               }catch (e: ApiException){
                   e.printStackTrace()
               }catch (e: NoInternetException){
                   e.printStackTrace()
               }
           }*/


    }


    fun onStoryFollowing(allUserData: AllUsersData) {
        lifecycleScope.launch {
            try {
                val res = homeRepositry.addUserFollow(userId, allUserData.usersId)
                if (res.status) {
                    getString(R.string.following).toast(this@StoryActivity)
                }
            } catch (e: ApiException) {
                e.printStackTrace()
            } catch (e: NoInternetException) {
                e.printStackTrace()
            }
        }
    }

    fun onViewSomeonesProfile(allUserData: AllUsersData) {
        val intent = Intent(this, SomeOneProfileActivity::class.java)
        intent.putExtra(SomeOneProfileActivity.SOME_ONES_USER_ID, allUserData.usersId)
        startActivity(intent)
    }

    fun onStoryClick() {
        startActivity(Intent(this, ViewStoryActivity::class.java))
    }

    override fun onBack() {
        finish()
    }
}