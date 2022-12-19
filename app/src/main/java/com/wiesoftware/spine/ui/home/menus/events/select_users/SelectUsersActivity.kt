package com.wiesoftware.spine.ui.home.menus.events.select_users

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
import com.wiesoftware.spine.databinding.ActivitySelectUsersBinding
import com.wiesoftware.spine.ui.home.menus.events.event_details.EVENT_ID
import com.wiesoftware.spine.ui.home.menus.spine.selectfollowers.SelectFollowersAdapter
import com.wiesoftware.spine.util.ApiException
import com.wiesoftware.spine.util.NoInternetException
import com.wiesoftware.spine.util.toast
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class  SelectUsersActivity : AppCompatActivity(),KodeinAware, SelectUsersEventListener,
    SelectFollowersAdapter.FollowersEventListener {

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base?.let { RuntimeLocaleChanger.wrapContext(it) })
    }

    override val kodein by kodein()
    val  factory: SelectUsersViewModelFactory by instance()
    val eventRepositry: EventRepository by instance()
    lateinit var binding: ActivitySelectUsersBinding
    lateinit var usr_id: String
    val usr_list: MutableList<FollowersData> = ArrayList<FollowersData>()
    val datalist: MutableMap<String,String> = HashMap<String,String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_select_users)
        binding=DataBindingUtil.setContentView(this,R.layout.activity_select_users)
        val viewmodel=ViewModelProvider(this,factory).get(SelectUsersViewModel::class.java)
        binding.viewmodel=viewmodel
        viewmodel.selectUsersEventListener=this

        viewmodel.getloggedInUser().observe(this, Observer { user->
            usr_id=user.users_id!!
            setFollowers()
        })
    }

    private fun setFollowers() {
        lifecycleScope.launch {
            try {
                val followersRes = eventRepositry.getFollowers(1, 100, usr_id)
                if (followersRes.status) {
                    val followersData = followersRes.data
                    binding.rvSelectedFollowers.also {
                        it.layoutManager = GridLayoutManager(this@SelectUsersActivity,2,
                            RecyclerView.VERTICAL,false)
                        /*LinearLayoutManager(
                        this@SelectFollowersActivity,
                        RecyclerView.VERTICAL,
                        false
                    )*/
                        it.setHasFixedSize(true)
                        it.adapter = SelectFollowersAdapter(followersData, this@SelectUsersActivity)
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
        val event_id=intent.getStringExtra(EVENT_ID)
        event_id?.let {
            datalist.put("user_id",usr_id)
            datalist.put("spine_event_id",event_id)
            var i=0
            usr_list.forEach {
                datalist.put("share_users_id[$i]",it.user_id)
                i++
            }
            lifecycleScope.launch {
                try {
                    val res=eventRepositry.spineEventShare(datalist)
                    if (res.status){
                        res.message.toast(this@SelectUsersActivity)
                        onBackPressed()
                    }
                }catch (e: ApiException){
                    e.printStackTrace()
                }catch (e: NoInternetException){
                    e.printStackTrace()
                }
            }
        }
    }

    override fun followerSelected(followersData: FollowersData) {
        usr_list.add(followersData)
    }
}