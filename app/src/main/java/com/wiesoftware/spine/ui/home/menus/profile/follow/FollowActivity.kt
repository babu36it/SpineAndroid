package com.wiesoftware.spine.ui.home.menus.profile.follow

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.tabs.TabLayoutMediator
import com.wiesoftware.spine.R
import com.wiesoftware.spine.RuntimeLocaleChanger
import com.wiesoftware.spine.data.repo.HomeRepository
import com.wiesoftware.spine.databinding.ActivityFollowBinding
import com.wiesoftware.spine.ui.home.menus.profile.follow.followers.FollowersFragment
import com.wiesoftware.spine.ui.home.menus.profile.follow.following.FollowingFragment
import com.wiesoftware.spine.ui.home.menus.profile.masseges.MessagesTabadapter
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class FollowActivity : AppCompatActivity(),KodeinAware, FollowEventListener {

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base?.let { RuntimeLocaleChanger.wrapContext(it) })
    }

    override val kodein by kodein()
    val factory: FollowViewmodelFactory by instance()
    val homeRepositry: HomeRepository by instance()
    var userId: String=""

    lateinit var binding: ActivityFollowBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_follow)
        binding=DataBindingUtil.setContentView(this,R.layout.activity_follow)
        val viewmodel=ViewModelProvider(this,factory).get(FollowViewmodel::class.java)
        binding.viewmodel=viewmodel
        viewmodel.followEventListener=this
        setUpViewPager()
        val followers= intent.getStringExtra("followers")
        val following= intent.getStringExtra("following")

        addTabs(followers,following)
        viewmodel.getLoggedInUser().observe(this, Observer { user->
            userId=user.users_id!!

            val name= user.name
            binding.textView160.text=name
        })

    }




    private fun addTabs(followers: String?, following: String?) {
        TabLayoutMediator(binding.tabLayout2,binding.viewpager2){tab, position ->
        }.attach()
        binding.tabLayout2.getTabAt(0)?.text = followers+" " +resources.getText(R.string.followers)
        binding.tabLayout2.getTabAt(1)?.text =following+" "+ resources.getText(R.string.following)
    }

    private fun setUpViewPager() {
        val adapter= MessagesTabadapter(this)
        adapter.addFragment(FollowersFragment())
        adapter.addFragment(FollowingFragment())
        binding.viewpager2.offscreenPageLimit=2
        binding.viewpager2.adapter=adapter
    }

    override fun onBack() {
        onBackPressed()
    }
}