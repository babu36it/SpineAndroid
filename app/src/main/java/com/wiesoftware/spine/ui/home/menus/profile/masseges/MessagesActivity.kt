package com.wiesoftware.spine.ui.home.menus.profile.masseges

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.tabs.TabLayoutMediator
import com.wiesoftware.spine.R
import com.wiesoftware.spine.RuntimeLocaleChanger
import com.wiesoftware.spine.databinding.ActivityMessagesBinding
import com.wiesoftware.spine.ui.home.menus.profile.masseges.eve_request.EveRequestFragment
import com.wiesoftware.spine.ui.home.menus.profile.masseges.msg.MsgFragment
import com.wiesoftware.spine.ui.home.menus.profile.tabs.ProfileTabAdapter
import com.wiesoftware.spine.ui.home.menus.profile.tabs.bookmark.BookmarkFragment
import com.wiesoftware.spine.ui.home.menus.profile.tabs.events.EventsFragment
import com.wiesoftware.spine.ui.home.menus.profile.tabs.podcasts.PodcastFragment
import com.wiesoftware.spine.ui.home.menus.profile.tabs.posts.PostsFragment
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein

class MessagesActivity : AppCompatActivity(),KodeinAware, MessagesEventListener {

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base?.let { RuntimeLocaleChanger.wrapContext(it) })
    }

    override val kodein by kodein()
    lateinit var binding: ActivityMessagesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_messages)
        binding=DataBindingUtil.setContentView(this,R.layout.activity_messages)
        val viewmodel=ViewModelProvider(this).get(MessagesViewmodel::class.java)
        binding.viewmodel=viewmodel
        viewmodel.messagesEventListener=this

        setUpViewPager()
        addTabs()
    }

    private fun addTabs() {
        TabLayoutMediator(binding.tabLayout2,binding.viewpager2){tab, position ->
        }.attach()
        binding.tabLayout2.getTabAt(0)?.text = resources.getText(R.string.message)
        binding.tabLayout2.getTabAt(1)?.text = resources.getText(R.string.events_requests)
    }

    private fun setUpViewPager() {
        val adapter= MessagesTabadapter(this)
        adapter.addFragment(MsgFragment())
        adapter.addFragment(EveRequestFragment())
        binding.viewpager2.offscreenPageLimit=2
        binding.viewpager2.adapter=adapter
    }

    override fun onBack() {
        onBackPressed()
    }
}