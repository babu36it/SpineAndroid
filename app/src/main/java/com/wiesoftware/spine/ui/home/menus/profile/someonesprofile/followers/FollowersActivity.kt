package com.wiesoftware.spine.ui.home.menus.profile.someonesprofile.followers

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wiesoftware.spine.R
import com.wiesoftware.spine.RuntimeLocaleChanger
import com.wiesoftware.spine.data.net.reponses.FollowersData
import com.wiesoftware.spine.data.repo.EventRepository
import com.wiesoftware.spine.data.repo.HomeRepository
import com.wiesoftware.spine.databinding.ActivityFollowersBinding
import com.wiesoftware.spine.ui.home.menus.profile.someonesprofile.SOMEONE_U_ID
import com.wiesoftware.spine.ui.home.menus.profile.someonesprofile.SOMEONE_U_NAME
import com.wiesoftware.spine.ui.home.menus.profile.someonesprofile.SomeOneProfileActivity
import com.wiesoftware.spine.ui.home.menus.spine.foryou.STORY_IMAGE
import com.wiesoftware.spine.util.*
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class FollowersActivity : AppCompatActivity(),KodeinAware, FollowersActivityEventListener,
    FFAdapter.FFEventListener {

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base?.let { RuntimeLocaleChanger.wrapContext(it) })
    }

    override val kodein by kodein()
    val factory: FollowersActivityViewmodelFactory by instance()
    val homeRepositry: HomeRepository by instance()
    val eventRepository: EventRepository by instance()
    lateinit var binding: ActivityFollowersBinding
    lateinit var cUserId: String
    var otherUserId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_followers)
        binding=DataBindingUtil.setContentView(this,R.layout.activity_followers)
        val viewmodel=ViewModelProvider(this,factory).get(FollowersActivityViewModel::class.java)
        binding.viewmodel=viewmodel
        viewmodel.followersActivityEventListener=this

        otherUserId= Prefs.getString(SOMEONE_U_ID,"")
        val userName= Prefs.getString(SOMEONE_U_NAME,"")
        val ACTI_NAME=intent.getStringExtra(SomeOneProfileActivity.ACTI_NAME)
        binding.textView251.text=ACTI_NAME
        val followers=getString(R.string.followers)
        if (ACTI_NAME!!.equals(followers)){
            getFollowers(otherUserId!!)
        }else{
            getFollowing(otherUserId!!)
        }

        viewmodel.getLoggedInUser().observe(this, Observer { user->
            cUserId=user.users_id!!
        })
    }

    private fun getFollowing(userId: String) {
        lifecycleScope.launch {
            try {
                val res=homeRepositry.getFollowingList(1,100,userId)
                if (res.status){
                    STORY_IMAGE=res.image
                    val dataList=res.data
                    binding.rvFfList.also {
                        it.layoutManager=LinearLayoutManager(this@FollowersActivity,RecyclerView.VERTICAL,false)
                        it.setHasFixedSize(true)
                        it.adapter=FFAdapter(dataList,this@FollowersActivity)
                    }
                }

            }catch (e: ApiException){
                e.printStackTrace()
            }catch (e: NoInternetException){
                e.printStackTrace()
            }
        }
    }

    private fun getFollowers(userId: String) {

            lifecycleScope.launch {
                try {
                    val res=eventRepository.getFollowers(1,100,userId)
                    if (res.status){
                        STORY_IMAGE =res.image
                        val dataList=res.data
                        binding.rvFfList.also {
                            it.layoutManager=LinearLayoutManager(this@FollowersActivity,RecyclerView.VERTICAL,false)
                            it.setHasFixedSize(true)
                            it.adapter=FFAdapter(dataList,this@FollowersActivity)

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



    override fun onFollow(followersData: FollowersData) {
        addUserFollow(followersData)
    }
    fun addUserFollow(followersData: FollowersData){
        val folloUserId=followersData.tbl_users_user_id
        lifecycleScope.launch {
            try {
                val res=homeRepositry.addUserFollow(cUserId,folloUserId)
            }catch (e: ApiException){
                e.printStackTrace()
            }catch (e: NoInternetException){
                e.printStackTrace()
            }
        }
    }
}